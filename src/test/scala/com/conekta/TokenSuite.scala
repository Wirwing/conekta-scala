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
class TokenSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("TokenSuite"));

  test("Tokens can be retreived individually") {

    val token = Token.find("tok_test_visa_4242")
    token.id should equal("tok_test_visa_4242")

  }

}
