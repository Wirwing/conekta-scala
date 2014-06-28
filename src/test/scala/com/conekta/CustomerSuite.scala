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
class CustomerSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("CustomerSuite"));

  ignore("All customers can be retreived") {
    val customers = Customer.all

    customers.foreach { customer =>
      customer.id should equal (customer.cards.head.customerId)
    }

  }

  ignore("Customers can be retreived individually") {

    val retreivedCustomer = Customer.retrieve("cus_oDaDzVH5d1L9upFVc")

    retreivedCustomer.id should be("cus_oDaDzVH5d1L9upFVc")

  }

  ignore("Customers can be created") {

    val customer = Customer.create(DefaultCustomerMap)

    customer.id should not be null
    customer.name should be("Scala Customer")

  }

  ignore("Customers can be updated") {

    val customer = Customer.create(DefaultCustomerMap)
    val updatedCustomer = customer.update(Map("name" -> "Updated Scala Customer"))
    updatedCustomer.name should equal("Updated Scala Customer")

  }

  ignore("Customers can be deleted") {
    val customer = Customer.create(DefaultCustomerMap)
    val deletedCustomer = customer.delete()
    deletedCustomer.deleted should be(true)
    deletedCustomer.id should equal(customer.id)
  }

  ignore("Customers can be listed") {
    val customers = Customer.all()
    customers.head.isInstanceOf[Customer] should be(true)
  }

  ignore("Add card to Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardToken)
    customer.cards.size should equal(1)
    customer.cards.last.last4 should equal("4242")

  }

  ignore("Update card from Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardToken)
    val updatedCard = card.update(Map("token" -> "tok_test_mastercard_4444"))
    updatedCard.last4 should equal("4444")

  }

  test("Delete card from Customer") {

    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardToken)
    
    val previousSize = customer.cards.size
    
    val deletedCard = card.delete

    deletedCard.deleted should be (true) 

  }

  test("Delete all customers") {
    val customers = Customer.all
    customers.foreach(costumer => costumer.delete)
  }

}