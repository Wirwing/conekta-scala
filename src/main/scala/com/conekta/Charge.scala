package com.conekta

import play.api.libs.json._
import play.api.libs.functional.syntax._

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
      (json \ "type").as[String] match {
        case "card_payment" => json.as[CardPayment]
        case "cash_payment" => json.as[OxxoPayment]
        case "bank_transfer_payment" => json.as[BankTransferPayment]
      }
    }

    def writes(payment: PaymentMethod): JsValue = JsObject(Seq(
      "name" -> JsString("name"),
      "type" -> JsString("type")))
      
  }

  //  implicit val paymentReads: Reads[PaymentMethod] = (__ \ "type").read[String].flatMap[PaymentMethod] {
  //    
  ////      (__ \ "x").read[Int].map(CardPayment)
  //  }

}

//case class Shipment(
//  carrier: Option[String],
//  service: Option[String],
//  trackingId: Option[String],
//  price: Option[Int],
//  address: Option[Address])
//
//object Shipment {
//  implicit val lineItemReads: Reads[Shipment] = (
//    (__ \ "carrier").readNullable[String] and
//    (__ \ "service").readNullable[String] and
//    (__ \ "tracking_id").readNullable[String] and
//    (__ \ "price").readNullable[Int] and
//    (__ \ "state").readNullable[Address])(Shipment.apply _)
//}
//
//case class Refunds(
//  createdAt: Int,
//  amount: Int,
//  currency: String,
//  transaction: String)
//
//object Refunds {
//  implicit val refundReads: Reads[Refunds] = (
//    (__ \ "created_at").read[Int] and
//    (__ \ "amount").read[Int] and
//    (__ \ "currency").read[String] and
//    (__ \ "transaction").read[String])(Refunds.apply _)
//}
//
//case class Address(
//  street1: Option[String],
//  stree2: Option[String],
//  street3: Option[String],
//  city: Option[String],
//  state: Option[String],
//  zip: Option[String],
//  country: Option[String],
//  taxId: Option[String],
//  companyName: Option[String],
//  phone: Option[String],
//  email: Option[String])
//
//object Address {
//  implicit val lineItemReads: Reads[Address] = (
//    (__ \ "street1").readNullable[String] and
//    (__ \ "street2").readNullable[String] and
//    (__ \ "street3").readNullable[String] and
//    (__ \ "city").readNullable[String] and
//    (__ \ "state").readNullable[String] and
//    (__ \ "zip").readNullable[String] and
//    (__ \ "country").readNullable[String] and
//    (__ \ "tax_id").readNullable[String] and
//    (__ \ "company_name").readNullable[String] and
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    
//    
//      //  implicit val paymentReads: Reads[PaymentMethod] = (
//  //    (__ \ "type").as[String] match {
//  //      case "card_payment" => _.as[CardPayment]
//  //      case "cash_payment" => _.as[OxxoPayment]
//  //      case "bank_transfer_payment" => _.as[BankTransferPayment]
//  //    })
//
//  implicit val paymentReads: Reads[PaymentMethod] = {
//    
//    
//    ((__ \ "type").read[String])((paymentType: String) => {
//      
//      paymentType match{
//        case "bank_transfer_payment" => {
//          
//        }
//      }
//      
//    })
//     
//    
//    null
////      case "bank_transfer_payment" => {
////      
////      (__ \ "service_name").read[String] and
////    (__ \ "service_number").read[String] and
////    (__ \ "reference").read[String])((serviceName: String, serviceNumber: String, reference: String ) => BankTransferPayment(serviceName, serviceNumber, reference))
//      
//    
//      
//  }
//    
//      
////      (__ \ "x").read[Int].map(Bar)
//      
////      
////      (__ \ "brand").read[String] and
////    (__ \ "auth_code").read[String] and
////    (__ \ "last4").read[String] and
////    (__ \ "exp_month").read[String] and
////    (__ \ "exp_year").read[String] and
////    (__ \ "name").read[String])((yes: Int, no: Int, maybe: Int) => new Vote(yes, no, maybe)) 
//      
////    }
////    case "Baz" => (__ \ "s").read[String].map(Baz)
////    case "Bah" => (__ \ "s").read[String].map(Bah)
//  }