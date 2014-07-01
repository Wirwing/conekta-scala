package com.conekta

import scala.collection.JavaConversions
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.UUID
import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair

@RunWith(classOf[JUnitRunner])
class ChargeSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("ChargeSerializationSuite"));

  test("Charges can be retreived individually") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val createdCharge = Charge.create(chargeData)
    createdCharge.status should be("paid")

    val charge = Charge.find(createdCharge.id)
    charge.id should be(createdCharge.id)

  }

  test("All charges can be retreived") {

    val charges = Charge.all
    charges.head.getClass().getSimpleName should be("Charge")

  }

  test("Charges can be queried") {

    val query = Map("description" -> "Scala Charge")

    val charges = Charge.where(query)
    charges.head.getClass().getSimpleName should be("Charge")

  }

  test("Charge with bank payment can be created") {

    val bankMap = Map("bank" -> Map("type" -> "banorte"))
    val chargeData = DefaultChargeMap ++ bankMap

    val charge = Charge.create(chargeData)
    charge.status should be("pending_payment")
    charge.paymentMethod.isInstanceOf[BankTransferPayment] should be(true)

  }

  test("Charge with card payment can be created") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap

    val charge = Charge.create(chargeData)
    charge.status should be("paid")
    charge.paymentMethod.isInstanceOf[CardPayment] should be(true)

  }

  test("Charge with oxxo payment can be created") {

    val oxxoMap = Map("cash" -> Map("type" -> "oxxo"))
    val chargeData = DefaultChargeMap ++ oxxoMap

    val charge = Charge.create(chargeData)
    charge.status should be("pending_payment")
    charge.paymentMethod.isInstanceOf[OxxoPayment] should be(true)

  }

  test("Unsuccesful charge with card payment") {

    val chargeData = InvalidChargeMap ++ DefaultCardMap
    intercept[Exception] {
      val charge = Charge.create(chargeData)
    }

  }

  test("Charge can be complete refunded successfully") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val amount = DefaultChargeMap.get("amount").get.asInstanceOf[Int]

    val charge = Charge.create(chargeData)
    charge.status should be("paid")

    val refundedCharge = charge.refund()
    refundedCharge.status should be("refunded")

    refundedCharge.refunds.head.amount should equal(-amount)

  }

  test("Charge can be complete partially refunded successfully") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val amount = DefaultChargeMap.get("amount").get.asInstanceOf[Int]

    val charge = Charge.create(chargeData)
    charge.status should be("paid")

    val firstRefundedAmount = 1000

    var refundedCharge = charge.refund(firstRefundedAmount)
    refundedCharge.status should be("partially_refunded")
    refundedCharge.refunds.size should equal(1)
    refundedCharge.refunds.head.amount should equal(-firstRefundedAmount)

    val secondRefundedAmount = 3000

    refundedCharge = charge.refund(secondRefundedAmount)
    refundedCharge.status should be("refunded")
    refundedCharge.refunds.size should equal(2)
    refundedCharge.refunds(1).amount should equal(-secondRefundedAmount)

  }

  ignore("Unsuccesful refund charge") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val amount = DefaultChargeMap.get("amount").get.asInstanceOf[Int]

    val charge = Charge.create(chargeData)
    charge.status should be("paid")

    intercept[Exception] {
      val refundedCharge = charge.refund(30000)
    }

  }

  test("Preauthorized charge can be captured") {

    val captureMap = Map("capture" -> false)
    
    val chargeData = DefaultChargeMap ++ DefaultCardMap ++ captureMap
    val charge = Charge.create(chargeData)
    charge.status should be("pre_authorized")

    val capturedCharge = charge.capture
    capturedCharge.status should be("paid")
    
  }

}