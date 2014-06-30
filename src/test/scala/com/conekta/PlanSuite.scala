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

  ignore("Plans can be retreived individually") {

    val createdPlan = Plan.create(getUniquePlanMap)
    val retreivedPlan = Plan.find(createdPlan.id)

    createdPlan should equal(retreivedPlan)

  }

  ignore("Plans can be queried") {

    val searchedPlans = Plan.where(Map("name" -> "Scala Plan"))
    searchedPlans.size should be(5)

  }

  ignore("Plans can be deleted") {

    val plan = Plan.create(getUniquePlanMap)
    val deletedPlan = plan.delete()
    deletedPlan.deleted should be(true)
    deletedPlan.id should equal(plan.id)

  }

  test("All plans can be deleted") {
    val plans = Plan.all
    plans.foreach(plan => plan.delete)
  }

  ignore("Customers subscription can be created with a plan") {

    val plan = Plan.create(getUniquePlanMap)
    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val subscription = customer.createSubscription(Map("plan" -> plan.id))
    subscription.planId should equal(plan.id)

  }

  ignore("A customer's existing plan can be replaced") {

    val originalPlan = Plan.create(getUniquePlanMap)
    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val subscription = customer.createSubscription(Map("plan" -> originalPlan.id))
    subscription.planId should equal(originalPlan.id)

    val newPlan = Plan.create(getUniquePlanMap)
    customer.createSubscription(Map("plan" -> newPlan.id))

    val updatedCustomer = Customer.find(customer.id)
    updatedCustomer.subscription.get.planId should equal(newPlan.id)

  }

  ignore("Customer's subscription can be canceled") {

    val plan = Plan.create(getUniquePlanMap)
    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val subscription = customer.createSubscription(Map("plan" -> plan.id))
    subscription.status should equal("in_trial")

    val updatedCustomer = customer.copy(subscription = Option(subscription))

    val canceledSubscription = updatedCustomer.subscription.get.cancel()
    canceledSubscription.status should be("canceled")

  }

  ignore("Customer's subscription can be paused") {

    val plan = Plan.create(getUniquePlanMap)
    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val subscription = customer.createSubscription(Map("plan" -> plan.id))
    subscription.status should equal("in_trial")

    val updatedCustomer = customer.copy(subscription = Option(subscription))

    val pausedSubscription = updatedCustomer.subscription.get.pause()
    pausedSubscription.status should be("paused")

  }

  test("Customer's subscription can be resumed") {

    val plan = Plan.create(getUniquePlanMap)
    val customer = Customer.create(DefaultCustomerMap)
    val card = customer.createCard(DefaultCardMap.getOrElse("card", "tok_test_visa_4242"))

    val subscription = customer.createSubscription(Map("plan" -> plan.id))
    subscription.status should equal("in_trial")

    val updatedCustomer = customer.copy(subscription = Option(subscription))

    val pausedSubscription = updatedCustomer.subscription.get.pause()
    pausedSubscription.status should be("paused")

    val resumedSubscription = updatedCustomer.subscription.get.resume()
    resumedSubscription.status should be("active")

  }

}
