package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class DeletedCard(id: String, deleted: Boolean)

object DeletedCard {
  implicit val deletedCardReads: Reads[DeletedCard] = (
    (__ \ "id").read[String] and
    (__ \ "deleted").read[Boolean])(DeletedCard.apply _)
}

case class Card(
  id: String,
  customerId: String,
  name: String,
  last4: String,
  expMonth: String,
  expYear: String) extends Resource {

  def update(updateParams: Map[String, _]): Card = {

    val url = parentChildURL("customer", customerId, this, this.id)
    request("PUT", url, updateParams).as[Card]
    
  }

  def delete: DeletedCard = {
    
    val url = parentChildURL("customer", customerId, this, this.id)
    val deletedCard = request("DELETE", url).as[DeletedCard]
    
    deletedCard

  }

}

object Card extends Resource {

  implicit val cardReads: Reads[Card] = (
    (__ \ "id").read[String] and
    (__ \ "customer_id").read[String] and
    (__ \ "name").read[String] and
    (__ \ "last4").read[String] and
    (__ \ "exp_month").read[String] and
    (__ \ "exp_year").read[String])(Card.apply _)

}
