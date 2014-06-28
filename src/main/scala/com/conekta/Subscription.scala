package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Subscription(
  id: String,
  status: String,
  createdAt: Int,
  billingCycleStart: Int,
  billingCycledEnd: Option[Int],
  planId: String,
  customerId: String,
  cardId: String) extends Resource {

  def update(updateParams: Map[String, _]): Subscription = {
    val url = parentChildURL("customer", customerId, this, this.id)
    request("PUT", url, updateParams).as[Subscription]
  }

  def resume(): Subscription = {
    request("POST", "%s/subscription/resume".format(parentURL("customer", customerId)), Map.empty).as[Subscription]
  }

  def pause(): Subscription = {
    request("POST", "%s/subscription/pause".format(parentURL("customer", customerId)), Map.empty).as[Subscription]
  }

  def cancel(): Subscription = {
    request("POST", "%s/subscription/cancel".format(parentURL("customer", customerId)), Map.empty).as[Subscription]
  }

}

object Subscription extends Resource {

  implicit val subscriptionReads: Reads[Subscription] = (
    (__ \ "id").read[String] and
    (__ \ "status").read[String] and
    (__ \ "created_at").read[Int] and
    (__ \ "billing_cycle_start").read[Int] and
    (__ \ "billing_cycle_end").readNullable[Int] and
    (__ \ "plan_id").read[String] and
    (__ \ "customer_id").read[String] and
    (__ \ "card_id").read[String])(Subscription.apply _)
}