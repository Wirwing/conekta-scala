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
class CardSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("CardSuite"));

  ignore("Add card to Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))
    customer.cards.size should equal(1)
    customer.cards.last.last4 should equal("4242")

  }

  ignore("Update card from Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))
    val updatedCard = card.update(Map("token" -> "tok_test_mastercard_4444"))
    updatedCard.last4 should equal("4444")

  }

  ignore("Delete card from Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val previousSize = customer.cards.size

    val deletedCard = card.delete

    deletedCard.deleted should be(true)

  }

}