package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Subscription(
  id: String,
  status: String,
  createdAt: Int,
  billingCycleStart: Int,
  billingCycledEnd: Int,
  planId: String,
  customerId: String,
  cardId: String) extends Resource

object Subscription extends Resource {

  implicit val subscriptionReads: Reads[Subscription] = (
    (__ \ "id").read[String] and
    (__ \ "status").read[String] and
    (__ \ "created_at").read[Int] and
    (__ \ "billing_cycle_start").read[Int] and
    (__ \ "billing_cycle_end").read[Int] and
    (__ \ "plan_id").read[String] and
    (__ \ "customer_id").read[String] and
    (__ \ "card_id").read[String])(Subscription.apply _)
}