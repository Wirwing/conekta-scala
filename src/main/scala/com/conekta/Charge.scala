package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Charge(
  id: String,
  createdAt: Int,
  status: Int,
  currency: String,
  description: Option[String],
  referenceId: String,
  failureCode: Option[String],
  failureMessage: Option[String],
  amount: Int,
  paymentMethod: PaymentMethod,
  details: Details,
  fee: Int,
  monthlyInstallments: Option[Int],
  refunds: Option[Refunds]) extends Resource

object Charge extends Resource {

  implicit val chargeReads: Reads[Charge] = (
    (__ \ "id").read[String] and
    (__ \ "created_at").read[Int] and
    (__ \ "status").read[Int] and
    (__ \ "currency").read[String] and
    (__ \ "description").readNullable[String] and
    (__ \ "reference_id").read[String] and
    (__ \ "failure_code").readNullable[String] and
    (__ \ "failure_message").readNullable[String] and
    (__ \ "amount").read[Int] and
    (__ \ "payment_method").read[PaymentMethod] and
    (__ \ "details").read[Details] and
    (__ \ "fee").read[Int] and
    (__ \ "monthly_installments").readNullable[Int] and
    (__ \ "refunds").readNullable[Refunds])(Charge.apply _)

  def find(id: String): Charge = request("GET", instanceURL(id)).as[Charge]

  def all(): List[Charge] = request("GET", classURL).as[List[Charge]]

}