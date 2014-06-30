package com.conekta

import scala.collection.JavaConversions
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.util.UUID
import com.typesafe.scalalogging.slf4j
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair

@RunWith(classOf[JUnitRunner])
class ChargeSerializationSuite extends FunSuite with ConektaSuite {

  val logger = Logger(LoggerFactory.getLogger("ChargeSerializationSuite"));

  test("Charge can be retreived individually for given Id"){
    
    val charge = Charge.find("53b0f5f2d7e1a0b475000223")
    
    
  }
  
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
    charges.head.getClass().getSimpleName should be("Charge")

  }

  ignore("Charges can be queried") {

    val query = Map("description" -> "Scala Charge")

    val charges = Charge.where(query)
    charges.size should be(2)
    charges.head.getClass().getSimpleName should be("Charge")

  }

  ignore("Charge with bank payment can be created") {

    val bankMap = Map("bank" -> Map("type" -> "banorte"))
    val chargeData = DefaultChargeMap ++ bankMap

    val charge = Charge.create(chargeData)
    charge.status should be("pending_payment")
    charge.paymentMethod.isInstanceOf[BankTransferPayment] should be(true)

  }

  ignore("Charge with card payment can be created") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap

    val charge = Charge.create(chargeData)
    charge.status should be("paid")
    charge.paymentMethod.isInstanceOf[CardPayment] should be(true)

  }

  ignore("Charge with oxxo payment can be created") {

    val oxxoMap = Map("cash" -> Map("type" -> "oxxo"))
    val chargeData = DefaultChargeMap ++ oxxoMap

    val charge = Charge.create(chargeData)
    charge.status should be("pending_payment")
    charge.paymentMethod.isInstanceOf[OxxoPayment] should be(true)

  }

  ignore("Unsuccesful charge with card payment") {

    val chargeData = InvalidChargeMap ++ DefaultCardMap
    intercept[Exception] {
      val charge = Charge.create(chargeData)
    }

  }

  ignore("Charge can be complete refunded successfully") {

    val chargeData = DefaultChargeMap ++ DefaultCardMap
    val amount = DefaultCardMap.get("amount")

    val charge = Charge.create(chargeData)
    charge.status should be("paid")

    charge.paymentMethod.isInstanceOf[CardPayment] should be(true)

    val refundedCharge = charge.refund()
    refundedCharge.status should be("refunded")

    refundedCharge.refunds.get.amount should be (amount)
    
  }

  //  describe :charge_tests do
  //    p "charge tests"
  //    before :each do
  //      @valid_payment_method = {amount: 2000, currency: 'mxn', description: 'Some desc'}
  //      @invalid_payment_method = {amount: 10, currency: 'mxn', description: 'Some desc'}
  //      @valid_visa_card = {card: 'tok_test_visa_4242'}
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

//ignore("Nested serialization"){
//    
//    val chargeData = DefaultChargeMap + ("bank" -> Map("type" -> "banorte"))
//    logger.debug("Data before resource: " + chargeData.toString)
//    
//    
//    val paramList: List[(String, String)] = chargeData.flatMap(kv => Resource.flattenParam(kv._1, kv._2)).toList
//    logger.debug(paramList.toString)
//    
//    val postParamList: List[BasicNameValuePair] = paramList.map(kv => new BasicNameValuePair(kv._1, kv._2))
//    
//    logger.debug(postParamList.toString)
//    
//    var entity: UrlEncodedFormEntity = new UrlEncodedFormEntity(JavaConversions.seqAsJavaList(postParamList), "UTF-8")
//    
////    logger.debug(entity.getContent())
//    
//  }
