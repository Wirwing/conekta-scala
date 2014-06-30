package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Charge(
  id: String,
  createdAt: Int,
  status: String,
  currency: String,
  description: String,
  referenceId: Option[String],
  failureCode: Option[String],
  failureMessage: Option[String],
  amount: Int,
  paymentMethod: PaymentMethod,
  details: Details,
  fee: Int,
  monthlyInstallments: Option[Int],
  refunds: Option[Refunds]) extends Resource {

  def refund(amount: Int = 0): Charge = {

    val paramOrDefault = amount match {
      case value if value > 0 => Map("amount" -> value)
      case _ => Map.empty[String, Int]
    }

    request("POST", "%s/refund".format(instanceURL(this.id)), paramOrDefault).as[Charge]
  }
}

object Charge extends Resource {

  implicit val chargeReads: Reads[Charge] = (
    (__ \ "id").read[String] and
    (__ \ "created_at").read[Int] and
    (__ \ "status").read[String] and
    (__ \ "currency").read[String] and
    (__ \ "description").read[String] and
    (__ \ "reference_id").readNullable[String] and
    (__ \ "failure_code").readNullable[String] and
    (__ \ "failure_message").readNullable[String] and
    (__ \ "amount").read[Int] and
    (__ \ "payment_method").read[PaymentMethod] and
    (__ \ "details").read[Details] and
    (__ \ "fee").read[Int] and
    (__ \ "monthly_installments").readNullable[Int] and
    (__ \ "refunds").readNullable[Refunds])(Charge.apply _)

  def create(params: Map[String, _]): Charge = request("POST", classURL, params).as[Charge]

  def where(params: Map[String, _]): List[Charge] = request("GET", classURL, params).as[List[Charge]]

  def find(id: String): Charge = request("GET", instanceURL(id)).as[Charge]

  def all(): List[Charge] = request("GET", classURL).as[List[Charge]]

}