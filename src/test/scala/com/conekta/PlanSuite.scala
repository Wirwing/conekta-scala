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
class PlanSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("PlanSuite"));

  ignore("Plans can be created") {

    val plan = Plan.create(getUniquePlanMap)
    plan.name should equal("Scala Plan")

  }

}
