package com.conekta

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

object Resource {
  val ApiBase = "https://api.conekta.io"
  val BindingsVersion = "0.3.0"
  val CharSet = "UTF-8"
}

abstract class Resource {

  val logger = Logger(LoggerFactory.getLogger("Resource"));

  //Resource utility methods
  val className = this.getClass.getSimpleName.toLowerCase.replace("$", "")

  def className(any: Any): String = {
    any.getClass.getSimpleName.toLowerCase.replace("$", "")
  }

  val classURL = "%s/%ss".format(Resource.ApiBase, className)
  val singleInstanceURL = "%s/%s".format(Resource.ApiBase, className)

  def instanceURL(id: String) = "%s/%s".format(classURL, id)

  def parentURL(parentName: Any, parentId: String) = {
    "%s/%ss/%s".format(Resource.ApiBase, parentName, parentId)
  }

  def parentChildURL(parentName: Any, parentId: String, child: Any, childId: String) = {
    "%s/%ss/%s/%ss/%s".format(Resource.ApiBase, parentName, parentId, className(child), childId)
  }

  def request(method: String, url: String, params: Map[String, _] = Map.empty): JsValue = {
    val (body, rCode) = Requestor.rawRequest(method, url, params)
    interpretResponse(body, rCode)
  }

  private def interpretResponse(rBody: String, rCode: Int): JsValue = {
    val jsonAST = Json.parse(rBody)
    if (rCode < 200 || rCode >= 300) handleAPIError(rBody, rCode, jsonAST)
    jsonAST
  }

  private def handleAPIError(rBody: String, rCode: Int, jsonAST: JsValue) {
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