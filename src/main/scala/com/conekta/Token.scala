package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Token(
  id: String,
  liveMode: Boolean,
  used: Boolean) extends Resource

object Token extends Resource {

  implicit val tokenReads: Reads[Token] = (
    (__ \ "id").read[String] and
    (__ \ "live_mode").read[Boolean] and
    (__ \ "used").read[Boolean])(Token.apply _)
  
  def find(id: String): Token = request("GET", instanceURL(id)).as[Token]

}
