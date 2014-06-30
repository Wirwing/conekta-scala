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

  ignore("Charges can be retreived individually") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val createdCharge = Charge.create(chargeData)
    createdCharge.status should be("paid")

    val charge = Charge.find(createdCharge.id)
    charge.id should be(createdCharge.id)

    //    val paymentMethod = charge.paymentMethod.asInstanceOf[CardPayment] 
    //    logger.info(paymentMethod.authCode)

  }

  ignore("All charges can be retreived") {

    val charges = Charge.all
    charges.head.getClass().getSimpleName should be ("Charge")

  }

  ignore("Charges can be queried") {
    
    val query = Map("description" -> "Scala Charge")
    
    val charges = Charge.where(query)
    charges.size should be (2)
    charges.head.getClass().getSimpleName should be ("Charge")
    
  }
  
  test("Charge with bank payment can be created"){
    
    val bankMap = Map("bank" -> Map("type" -> "banorte"))
    val chargeData = DefaultChargeMap ++ bankMap
    
    logger.info(chargeData.toString)
    
    val charge = Charge.create(chargeData)
    charge.status should be ("pending_payment")

    //    charge.paymentMethod.isInstanceOf[BankTransferPayment] should be (true)
    
  }

  //  describe :charge_tests do
  //    p "charge tests"
  //    before :each do
  //      @valid_payment_method = {amount: 2000, currency: 'mxn', description: 'Some desc'}
  //      @invalid_payment_method = {amount: 10, currency: 'mxn', description: 'Some desc'}
  //      @valid_visa_card = {card: 'tok_test_visa_4242'}
  //    end
  //    it "tests succesful bank pm create" do
  //      pm = @valid_payment_method
  //      bank = {bank: {'type' => 'banorte'}}
  //      bpm = Conekta::Charge.create(pm.merge(bank))
  //      bpm.status.should eq("pending_payment")
  //    end
  //    it "tests succesful card pm create" do
  //      pm = @valid_payment_method
  //      card = @valid_visa_card
  //      cpm = Conekta::Charge.create(pm.merge(card))
  //      cpm.status.should eq("paid")
  //    end
  //    it "tests succesful oxxo pm create" do
  //      pm = @valid_payment_method
  //      oxxo = {cash: {'type' => 'oxxo'}}
  //      bpm = Conekta::Charge.create(pm.merge(oxxo))
  //      bpm.status.should eq("pending_payment")
  //    end
  //    it "test unsuccesful pm create" do
  //      pm = @invalid_payment_method
  //      card = @valid_visa_card
  //      begin
  //        cpm = Conekta::Charge.create(pm.merge(card))
  //      rescue Conekta::Error => e
  //        e.message.should eq("The minimum for card payments is 3 pesos. Check that the amount is in cents as explained in the documentation.")
  //      end
  //    end
  //    it "test susccesful refund" do
  //      pm = @valid_payment_method
  //      card = @valid_visa_card
  //      cpm = Conekta::Charge.create(pm.merge(card))
  //      cpm.status.should eq("paid")
  //      cpm.refund
  //      cpm.status.should eq("refunded")
  //    end
  //    it "test unsusccesful refund" do
  //      pm = @valid_payment_method
  //      card = @valid_visa_card
  //      cpm = Conekta::Charge.create(pm.merge(card))
  //      cpm.status.should eq("paid")
  //      begin
  //        cpm.refund(3000)
  //      rescue Conekta::Error => e
  //        e.message.should eq("The order does not exist or the amount to refund is invalid")
  //      end
  //    end
  //    it "tests succesful card pm create" do
  //      pm = @valid_payment_method
  //      card = @valid_visa_card
  //      capture = {capture: false}
  //      cpm = Conekta::Charge.create(pm.merge(card).merge(capture))
  //      cpm.status.should eq("pre_authorized")
  //      cpm.capture
  //      cpm.status.should eq("paid")
  //    end
  //  end

}