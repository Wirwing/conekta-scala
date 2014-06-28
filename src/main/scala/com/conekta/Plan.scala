package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class DeletedPlan(id: String, deleted: Boolean)

object DeletedPlan {
  implicit val cardReads: Reads[DeletedCard] = (
    (__ \ "id").read[String] and
    (__ \ "deleted").read[Boolean])(DeletedCard.apply _)
}

case class Plan(
  id: String,
  liveMode: Boolean,
  createdAt: Int,
  name: String,
  interval: String,
  frequency: Option[Int],
  intervalTotalCount: Option[Int],
  trialPeriodDays: Option[Int],
  currency: String,
  amount: Int,
  expiryCount: Option[Int]) extends Resource

object Plan extends Resource {

  implicit val cardReads: Reads[Plan] = (
    (__ \ "id").read[String] and
    (__ \ "livemode").read[Boolean] and
    (__ \ "created_at").read[Int] and
    (__ \ "name").read[String] and
    (__ \ "interval").read[String] and
    (__ \ "frequency").readNullable[Int] and
    (__ \ "interval_total_count").readNullable[Int] and
    (__ \ "trial_period_days").readNullable[Int] and
    (__ \ "currency").read[String] and
    (__ \ "amount").read[Int] and
    (__ \ "expiry_count").readNullable[Int])(Plan.apply _)

  def create(params: Map[String, _]): Plan = {
    return request("POST", classURL, params).as[Plan]
  }

}
