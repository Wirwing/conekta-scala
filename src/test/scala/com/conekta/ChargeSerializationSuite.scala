package com.conekta

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.UUID
import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(classOf[JUnitRunner])
class ChargeSerializationSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("ChargeSerializationSuite"));

  test("Deseralize payment") {

    val charge = Charge.find("53a8cae1d7e1a07a52001683")
    val paymentMethod = charge.paymentMethod.asInstanceOf[CardPayment] 
    
    logger.info(paymentMethod.authCode)
    charge.id should be ("53a8cae1d7e1a07a52001683")
    charge.status should be ("paid")
    paymentMethod.last4 should be ("4242")

  }

}