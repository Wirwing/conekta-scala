package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

sealed abstract class ConektaException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
case class APIException(msg: String, cause: Throwable = null) extends ConektaException(msg, cause)
case class NoConnectionException(msg: String, cause: Throwable = null) extends ConektaException(msg, cause)
case class AuthenticationException(msg: String) extends ConektaException(msg)
case class ParamaterValidationException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)
case class ProcessingException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)
case class ResourceNotFoundException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)
case class MalformedRequestException(msg: String, code: Option[String] = None, param: Option[String] = None) extends ConektaException(msg)

case class Error(errorType: Option[String], message: String, code: Option[String], param: Option[String])

object Error extends {

  implicit val errorReads: Reads[Error] = (
    (__ \ "type").readNullable[String] and
    (__ \ "message").read[String] and
    (__ \ "code").readNullable[String] and
    (__ \ "param").readNullable[String])(Error.apply _)

}