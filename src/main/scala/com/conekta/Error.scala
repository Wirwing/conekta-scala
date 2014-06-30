package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Error(errorType: String, message: String, code: Option[String], param: Option[String])

object Error extends {

  implicit val errorReads: Reads[Error] = (
    (__ \ "type").read[String] and
    (__ \ "message").read[String] and
    (__ \ "code").readNullable[String] and
    (__ \ "param").readNullable[String])(Error.apply _)

}