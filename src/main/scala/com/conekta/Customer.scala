package com.conekta

import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json._
import org.joda.convert.ToString

/**
 *
 * @author Wirwing
 */
case class DeletedCustomer(id: String, deleted: Boolean)

object DeletedCustomer {
  implicit val customerReads: Reads[DeletedCustomer] = (
    (__ \ "id").read[String] and
    (__ \ "deleted").read[Boolean])(DeletedCustomer.apply _)
}

case class Customer(
  id: String,
  email: String,
  name: String,
  phone: String,
  livemode: Boolean,
  defaultCardId: Option[String],
  createdAt: Int,
  var cards: List[Card],
  subscription: Option[Subscription]) extends Resource {

  def update(params: Map[String, _]): Customer = request("PUT", instanceURL(this.id), params).as[Customer]

  def delete(): DeletedCustomer = request("DELETE", instanceURL(this.id)).as[DeletedCustomer]

  def createCard(token: String): Card = {
    val params = Map("token" -> token)
    val card = request("POST", "%s/cards".format(instanceURL(id)), params).as[Card]
    val updatedCards = cards ::: List(card)
    this.cards = updatedCards
    card
  }

  def createSubscription(params: Map[String, _]): Subscription = {
    request("POST", "%s/subscription".format(instanceURL(id)), params).as[Subscription]
  }

}

object Customer extends Resource {

  implicit val customerReads: Reads[Customer] = (
    (__ \ "id").read[String] and
    (__ \ "email").read[String] and
    (__ \ "name").read[String] and
    (__ \ "phone").read[String] and
    (__ \ "livemode").read[Boolean] and
    (__ \ "default_card_id").readNullable[String] and
    (__ \ "created_at").read[Int] and
    (__ \ "cards").read[List[Card]] and
    (__ \ "subscription").readNullable[Subscription])(Customer.apply _)

  def where(params: Map[String, _]): List[Customer] = request("GET", classURL, params).as[List[Customer]]

  def create(params: Map[String, _]): Customer = request("POST", classURL, params).as[Customer]

  def find(id: String): Customer = request("GET", instanceURL(id)).as[Customer]

  def all(): List[Customer] = request("GET", classURL).as[List[Customer]]

}