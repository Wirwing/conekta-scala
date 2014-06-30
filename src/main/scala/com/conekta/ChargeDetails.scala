package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Address(
  street1: Option[String],
  stree2: Option[String],
  street3: Option[String],
  city: Option[String],
  state: Option[String],
  zip: Option[String],
  country: Option[String],
  taxId: Option[String],
  companyName: Option[String],
  phone: Option[String],
  email: Option[String])

object Address {
  implicit val lineItemReads: Reads[Address] = (
    (__ \ "street1").readNullable[String] and
    (__ \ "street2").readNullable[String] and
    (__ \ "street3").readNullable[String] and
    (__ \ "city").readNullable[String] and
    (__ \ "state").readNullable[String] and
    (__ \ "zip").readNullable[String] and
    (__ \ "country").readNullable[String] and
    (__ \ "tax_id").readNullable[String] and
    (__ \ "company_name").readNullable[String] and
    (__ \ "phone").readNullable[String] and
    (__ \ "email").readNullable[String])(Address.apply _)

}

case class Refunds(
  createdAt: Option[Int],
  amount: Option[Int],
  currency: Option[String],
  transaction: Option[String])

object Refunds {
  implicit val refundReads: Reads[Refunds] = (
    (__ \ "created_at").readNullable[Int] and
    (__ \ "amount").readNullable[Int] and
    (__ \ "currency").readNullable[String] and
    (__ \ "transaction").readNullable[String])(Refunds.apply _)
}

case class LineItem(
  name: Option[String],
  sku: Option[String],
  unitPrice: Option[Int],
  description: Option[String],
  quantity: Option[Int],
  itemType: Option[Int],
  category: Option[String])

object LineItem {
  implicit val itemReads: Reads[LineItem] = (
    (__ \ "name").readNullable[String] and
    (__ \ "sku").readNullable[String] and
    (__ \ "unit_price").readNullable[Int] and
    (__ \ "description").readNullable[String] and
    (__ \ "quantity").readNullable[Int] and
    (__ \ "type").readNullable[Int] and
    (__ \ "category").readNullable[String])(LineItem.apply _)

}

case class BankTransferPayment(
  serviceName: String,
  serviceNumber: String,
  reference: String) extends PaymentMethod

object BankTransferPayment {
  implicit val bankPaymentReads: Reads[BankTransferPayment] = (
    (__ \ "service_name").read[String] and
    (__ \ "service_number").read[String] and
    (__ \ "reference").read[String])(BankTransferPayment.apply _)
}

case class OxxoPayment(
  barcode: String,
  barcodeUrl: String,
  expiryDate: String,
  expiresAt: Int) extends PaymentMethod

object OxxoPayment {
  implicit val oxxoPaymentReads: Reads[OxxoPayment] = (
    (__ \ "barcode").read[String] and
    (__ \ "barcode_url").read[String] and
    (__ \ "expiry_date").read[String] and
    (__ \ "expires_at").read[Int])(OxxoPayment.apply _)
}

case class CardPayment(
  brand: String,
  authCode: String,
  last4: String,
  expMonth: String,
  expYear: String,
  name: String) extends PaymentMethod

object CardPayment {
  implicit val cardPaymentReads: Reads[CardPayment] = (
    (__ \ "brand").read[String] and
    (__ \ "auth_code").read[String] and
    (__ \ "last4").read[String] and
    (__ \ "exp_month").read[String] and
    (__ \ "exp_year").read[String] and
    (__ \ "name").read[String])(CardPayment.apply _)
}

sealed trait PaymentMethod
object PaymentMethod {

  implicit object PaymentFormat extends Format[PaymentMethod] {

    def reads(json: JsValue) = JsSuccess {
      (json \ "object").as[String] match {
        case "card_payment" => json.as[CardPayment]
        case "cash_payment" => json.as[OxxoPayment]
        case "bank_transfer_payment" => json.as[BankTransferPayment]
      }
    }

    def writes(payment: PaymentMethod): JsValue = JsObject(Seq(
      "name" -> JsString("name"),
      "type" -> JsString("type")))

  }

}

case class Shipment(
  carrier: Option[String],
  service: Option[String],
  trackingId: Option[String],
  price: Option[Int],
  address: Option[Address])

object Shipment {
  implicit val lineItemReads: Reads[Shipment] = (
    (__ \ "carrier").readNullable[String] and
    (__ \ "service").readNullable[String] and
    (__ \ "tracking_id").readNullable[String] and
    (__ \ "price").readNullable[Int] and
    (__ \ "state").readNullable[Address])(Shipment.apply _)
}

case class Details(
  name: Option[String],
  phone: Option[String],
  email: Option[String],
  dateOfBirth: Option[String],
  lineItems: Option[List[LineItem]],
  address: Option[Address],
  shipment: Option[Shipment])

object Details {
  implicit val detailReads: Reads[Details] = (
    (__ \ "name").readNullable[String] and
    (__ \ "phone").readNullable[String] and
    (__ \ "email").readNullable[String] and
    (__ \ "date_of_birth").readNullable[String] and
    (__ \ "line_items").readNullable[List[LineItem]] and
    (__ \ "address").readNullable[Address] and
    (__ \ "shipment").readNullable[Shipment])(Details.apply _)

}