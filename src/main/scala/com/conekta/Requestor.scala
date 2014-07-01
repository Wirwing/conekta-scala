package com.conekta

import java.net.URLEncoder
import scala.collection.JavaConversions.asJavaCollection
import scala.collection.JavaConversions.seqAsJavaList
import scala.util.Properties
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.params.ClientPNames
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.CoreConnectionPNames
import org.apache.http.params.CoreProtocolPNames
import org.apache.http.params.SyncBasicHttpParams
import org.apache.http.util.EntityUtils
import play.api.libs.json.Json
import org.apache.http.client.methods.HttpDeleteWithBody

object Requestor {

  private def urlEncodePair(k: String, v: String) = "%s=%s".format(URLEncoder.encode(k, Resource.CharSet), URLEncoder.encode(v, Resource.CharSet))

  private def flattenParam(k: String, v: Any): List[(String, String)] = {
    v match {
      case None => Nil
      case m: Map[_, _] => m.flatMap(kv => flattenParam("%s[%s]".format(k, kv._1), kv._2)).toList
      case _ => List((k, v.toString))
    }
  }

  private def httpClient: DefaultHttpClient = {
    if (apiKey == null || apiKey.isEmpty) {
      throw AuthenticationException("No API key provided. (HINT: set your API key using 'conekta.apiKey = <API-KEY>'. You can generate API keys from the Conekta web interface.")
    }

    //debug headers
    val javaPropNames = List("os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor")
    val javaPropMap = javaPropNames.map(n => (n.toString, Properties.propOrEmpty(n).toString)).toMap
    val fullPropMap = javaPropMap + (
      "scala.version" -> Properties.scalaPropOrEmpty("version.number"),
      "bindings.version" -> Resource.BindingsVersion,
      "lang" -> "scala",
      "publisher" -> "conekta")

    val defaultHeaders = asJavaCollection(List(
      new BasicHeader("X-Conekta-Client-User-Agent", Json.stringify(Json.toJson(fullPropMap))),
      new BasicHeader("User-Agent", "Conekta/v1 ScalaBindings/%s".format(Resource.BindingsVersion)),
      new BasicHeader("Authorization", "Bearer %s".format(apiKey))))

    val httpParams = new SyncBasicHttpParams().
      setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders).
      setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Resource.CharSet).
      setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000). //30 seconds
      setParameter(CoreConnectionPNames.SO_TIMEOUT, 80000) //80 seconds

    new DefaultHttpClient(connectionManager, httpParams)
  }

  private def getRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    new HttpGet("%s?%s".format(url, paramList.map(kv => urlEncodePair(kv._1, kv._2)).mkString("&")))
  }

  private def postRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPost(url)
    val postParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(postParamList), Resource.CharSet))
    request
  }

  private def putRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPut(url)
    val putParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(putParamList), Resource.CharSet))
    request
  }

  def deleteRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpDeleteWithBody(url)
    val deleteParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(deleteParamList), Resource.CharSet))
    request
  }

  def rawRequest(method: String, url: String, params: Map[String, _] = Map.empty): (String, Int) = {

    val client = httpClient
    val paramList = params.flatMap(kv => flattenParam(kv._1, kv._2)).toList
    try {
      val request = method.toLowerCase match {
        case "get" => getRequest(url, paramList)
        case "post" => postRequest(url, paramList)
        case "put" => putRequest(url, paramList)
        case "delete" => deleteRequest(url, paramList)
        case _ => throw new APIException("Unrecognized HTTP method %s. This may indicate a bug in the Conekta bindings.".format(method))
      }
      val response = client.execute(request)
      val entity = response.getEntity
      val body = EntityUtils.toString(entity)
      EntityUtils.consume(entity)
      (body, response.getStatusLine.getStatusCode)

    } catch {
      case e @ (_: java.io.IOException | _: ClientProtocolException) => throw NoConnectionException("Could not connect to Conekta (%s). Please check your internet connection and try again. If this problem persists, you should check Conekta's service status at https://twitter.com/conektaio".format(Resource.ApiBase), e)
    } finally {
      client.getConnectionManager.shutdown()
    }
  }

}