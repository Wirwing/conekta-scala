package com.conekta

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.UUID
import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory

trait ConektaSuite extends ShouldMatchers {

  apiKey = "key_VQspiT1DoYVtRGZG"

  val DefaultCardMap = Map("card" -> "tok_test_visa_4242")
  
  val InvalidChargeMap = Map("amount" -> 10, "currency" -> "mxn", "description" -> "Scala Charge")
  
  val DefaultChargeMap = Map("amount" -> 4000, "currency" -> "mxn", "description" -> "Scala Charge")

  val DefaultCustomerMap = Map("name" -> "Scala Conekta Customer", "email" -> "scala@typesafe.com", "phone" -> "55-5555-5555")

  private val DefaultPlanMap = Map("name" -> "Scala Plan", "amount" -> 10000, "currency" -> "MXN", "interval" -> "month", "frequency" -> 10, "trial_period_days" -> 15, "expiry_count" -> 12)

  private def getUniquePlanId(): String = return "PLAN-%s".format(UUID.randomUUID())

  def getUniquePlanMap(): Map[String, _] = return DefaultPlanMap + ("id" -> getUniquePlanId())

}