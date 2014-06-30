package com.conekta

import java.net.URLEncoder

import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import scala.util.Properties

import org.apache.commons.codec.binary.Base64
import org.apache.http.client._
import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.client.params._
import org.apache.http.client.entity._
import org.apache.http.params._
import org.apache.http.message._
import org.apache.http.util._
import play.api.libs.functional.syntax._
import play.api.libs.json._

sealed abstract class ConektaException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
case class APIException(msg: String, cause: Throwable = null) extends ConektaException(msg, cause)
case class NoConnectionException(msg: String, cause: Throwable = null) extends ConektaException(msg, cause)
case class AuthenticationException(msg: String) extends ConektaException(msg)
case class ParamaterValidationException(msg: String) extends ConektaException(msg)
case class ProcessingException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)
case class ResourceNotFoundException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)
case class MalformedRequestException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)

abstract class Resource {

  val logger = Logger(LoggerFactory.getLogger("Resource"));

  val ApiBase = "https://api.conekta.io"
  val BindingsVersion = "0.3.0"
  val CharSet = "UTF-8"

  //Resource utility methods
  def base64(in: String) = new String(Base64.encodeBase64(in.getBytes(CharSet)))
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
    jsonAST
  }

  def handleAPIError(rBody: String, rCode: Int, jsonAST: JsValue) {
    val error = try {
       jsonAST.as[List[Error]].head
    } catch {
      case e: JsResultException => throw new APIException(
        "Unable to parse response body from API: %s (HTTP response code was %s)".format(rBody, rCode), e)
    }
    rCode match {
      case 400 => throw new MalformedRequestException(error.message, param=error.param)
      case 401 => throw new AuthenticationException(error.message)
      case 402 => throw new ProcessingException(error.message, code=error.code, param=error.param)
      case 404 => throw new ResourceNotFoundException(error.message, code=error.code, param=error.param)
      case 500 => throw new APIException(error.message, null)
      case _ => throw new APIException(error.message, null)
    }
  }
  
}