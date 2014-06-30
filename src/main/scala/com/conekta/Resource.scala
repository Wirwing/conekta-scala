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
import org.slf4j.LoggerFactory

import com.typesafe.scalalogging.slf4j.Logger

import play.api.libs.json.JsResultException
import play.api.libs.json.JsValue
import play.api.libs.json.Json

abstract class Resource {

  val logger = Logger(LoggerFactory.getLogger("Resource"));

  val ApiBase = "https://api.conekta.io"
  val BindingsVersion = "0.3.0"
  val CharSet = "UTF-8"

  //Resource utility methods
  def urlEncodePair(k: String, v: String) = "%s=%s".format(URLEncoder.encode(k, CharSet), URLEncoder.encode(v, CharSet))
  val className = this.getClass.getSimpleName.toLowerCase.replace("$", "")

  def className(any: Any): String = {
    any.getClass.getSimpleName.toLowerCase.replace("$", "")
  }

  val classURL = "%s/%ss".format(ApiBase, className)
  val singleInstanceURL = "%s/%s".format(ApiBase, className)

  def instanceURL(id: String) = "%s/%s".format(classURL, id)

  def parentURL(parentName: Any, parentId: String) = {
    "%s/%ss/%s".format(ApiBase, parentName, parentId)
  }

  def parentChildURL(parentName: Any, parentId: String, child: Any, childId: String) = {
    "%s/%ss/%s/%ss/%s".format(ApiBase, parentName, parentId, className(child), childId)
  }

  def flattenParam(k: String, v: Any): List[(String, String)] = {
    v match {
      case None => Nil
      case m: Map[_, _] => m.flatMap(kv => flattenParam("%s[%s]".format(k, kv._1), kv._2)).toList
      case _ => List((k, v.toString))
    }
  }

  def httpClient: DefaultHttpClient = {
    if (apiKey == null || apiKey.isEmpty) {
      throw AuthenticationException("No API key provided. (HINT: set your API key using 'conekta.apiKey = <API-KEY>'. You can generate API keys from the Conekta web interface.")
    }

    //debug headers
    val javaPropNames = List("os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor")
    val javaPropMap = javaPropNames.map(n => (n.toString, Properties.propOrEmpty(n).toString)).toMap
    val fullPropMap = javaPropMap + (
      "scala.version" -> Properties.scalaPropOrEmpty("version.number"),
      "bindings.version" -> BindingsVersion,
      "lang" -> "scala",
      "publisher" -> "conekta")

    val defaultHeaders = asJavaCollection(List(
      new BasicHeader("X-Conekta-Client-User-Agent", Json.stringify(Json.toJson(fullPropMap))),
      new BasicHeader("User-Agent", "Conekta/v1 ScalaBindings/%s".format(BindingsVersion)),
      new BasicHeader("Authorization", "Bearer %s".format(apiKey))))

    val httpParams = new SyncBasicHttpParams().
      setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders).
      setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, CharSet).
      setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000). //30 seconds
      setParameter(CoreConnectionPNames.SO_TIMEOUT, 80000) //80 seconds

    new DefaultHttpClient(connectionManager, httpParams)
  }

  def getRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    new HttpGet("%s?%s".format(url, paramList.map(kv => urlEncodePair(kv._1, kv._2)).mkString("&")))
  }

  def postRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPost(url)
    val postParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(postParamList), CharSet))
    request
  }

  def putRequest(url: String, paramList: List[(String, String)]): HttpRequestBase = {
    val request = new HttpPut(url)
    val putParamList = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
    request.setEntity(new UrlEncodedFormEntity(seqAsJavaList(putParamList), CharSet))
    request
  }

  def rawRequest(method: String, url: String, params: Map[String, _] = Map.empty): (String, Int) = {
    val client = httpClient
    val paramList = params.flatMap(kv => flattenParam(kv._1, kv._2)).toList

    logger.debug(paramList.toString)

    try {
      val request = method.toLowerCase match {
        case "get" => getRequest(url, paramList)
        case "post" => postRequest(url, paramList)
        case "put" => putRequest(url, paramList)
        case _ => throw new APIException("Unrecognized HTTP method %r. This may indicate a bug in the Conekta bindings.".format(method))
      }
      val response = client.execute(request)
      val entity = response.getEntity
      val body = EntityUtils.toString(entity)
      EntityUtils.consume(entity)
      (body, response.getStatusLine.getStatusCode)
    } catch {
      case e @ (_: java.io.IOException | _: ClientProtocolException) => throw NoConnectionException("Could not connect to Conekta (%s). Please check your internet connection and try again. If this problem persists, you should check Conekta's service status at https://twitter.com/conektaio".format(ApiBase), e)
    } finally {
      client.getConnectionManager.shutdown()
    }
  }

  def request(method: String, url: String, params: Map[String, _] = Map.empty): JsValue = {
    val (rBody, rCode) = rawRequest(method, url, params)
    interpretResponse(rBody, rCode)
  }

  def interpretResponse(rBody: String, rCode: Int): JsValue = {
    val jsonAST = Json.parse(rBody)
    if (rCode < 200 || rCode >= 300) handleAPIError(rBody, rCode, jsonAST)
    jsonAST
  }

  def handleAPIError(rBody: String, rCode: Int, jsonAST: JsValue) {
    val error = try {
      jsonAST.as[Error]
    } catch {
      case e: JsResultException => throw new APIException(
        "Unable to parse response body from API: %s (HTTP response code was %s)".format(rBody, rCode), e)
    }
    rCode match {
      case 400 => throw new MalformedRequestException(error.message, param = error.param)
      case 401 => throw new AuthenticationException(error.message)
      case 402 => throw new ProcessingException(error.message, code = error.code, param = error.param)
      case 404 => throw new ResourceNotFoundException(error.message, code = error.code, param = error.param)
      case 422 => throw new ParamaterValidationException(error.message, code = error.code, param = error.param)
      case 500 => throw new APIException(error.message, null)
      case _ => throw new APIException(error.message, null)
    }
  }

}