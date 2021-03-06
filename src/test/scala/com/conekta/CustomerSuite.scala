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

  test("All customers can be retreived") {
    val customers = Customer.all

    customers.foreach { customer =>
      customer.id should equal(customer.cards.head.customerId)
    }

  }

  test("Customers can be retreived individually") {

    val customer = Customer.create(DefaultCustomerMap)
    val retreivedCustomer = Customer.find(customer.id)
    retreivedCustomer.id should equal (retreivedCustomer.id)

  }

  test("Customers can be created") {

    val customer = Customer.create(DefaultCustomerMap)

    customer.id should not be null
    customer.name should be (DefaultCustomerMap.get("name").get)

  }

  test("Customers can be updated") {

    val customer = Customer.create(DefaultCustomerMap)
    val updatedCustomer = customer.update(Map("name" -> "Updated Scala Customer"))
    updatedCustomer.name should be ("Updated Scala Customer")

  }

  test("Customers can be deleted") {
    val customer = Customer.create(DefaultCustomerMap)
    val deletedCustomer = customer.delete()
    deletedCustomer.deleted should be(true)
    deletedCustomer.id should equal(customer.id)
  }

  test("Customers can be listed") {
    val customers = Customer.all()
    customers.head.isInstanceOf[Customer] should be(true)
  }

  test("All customers can be deleted") {
    val customers = Customer.all
    customers.foreach(costumer => costumer.delete)
  }

}