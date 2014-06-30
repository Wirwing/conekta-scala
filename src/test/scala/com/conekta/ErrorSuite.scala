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
class ErrorSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("ErrorSuite"));

  test("No id error") {
    intercept[ResourceNotFoundException] {
      val charge = Charge.find(null)
    }
  }

  ignore("No conection error") {

//    ApiBase = "http://localhost:3001"

    intercept[NoConnectionException] {
      val customer = Customer.create(DefaultCustomerMap)
    }

  }

  test("API error") {
    intercept[APIException] {
      val customer = Customer.create(DefaultCustomerMap ++ Map("cards" -> Map(0 -> "tok_test_visa_4242")))
    }
  }

  ignore("Authentication error") {
    apiKey = ""
    intercept[AuthenticationException] {
      val customer = Customer.create(DefaultCustomerMap ++ Map("cards" -> Map(0 -> "tok_test_visa_4242")))
    }
  }

  test("Parameter validation error") {
    intercept[ParamaterValidationException] {
      val customer = Plan.create(Map("id" -> "scala-invalid"))
    }
  }

  test("Processing error") {

    val captureMap = Map("capture" -> false)
    val chargeData = DefaultChargeMap ++ DefaultCardMap ++ captureMap
    val charge = Charge.create(chargeData)

    val capturedCharge = charge.capture
    capturedCharge.refund()

  }

  test("Resource not found error") {
    intercept[ResourceNotFoundException] {
      val charges = Charge.find("1")
    }
  }

}