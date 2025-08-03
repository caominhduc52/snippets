package com.duccao.demo.share.helpers;

import com.duccao.avro.fltech.ms.teller.Account;
import com.duccao.avro.fltech.ms.teller.EventKey;
import com.duccao.avro.fltech.ms.teller.TransactionAmount;
import com.duccao.avro.fltech.ms.teller.TransactionAssessmentEvent;
import com.duccao.avro.fltech.ms.teller.TransactionAssessmentEventData;
import com.duccao.avro.fltech.ms.teller.TransactionAssessmentStatus;
import com.duccao.avro.fltech.ms.teller.TransactionDetails;
import com.duccao.avro.fltech.ms.teller.TransactionOutcome;
import com.duccao.avro.fltech.ms.teller.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for generating mock TransactionAssessmentEvent data.
 */
public class TransactionAssessmentEventHelper {

  /**
   * Creates a mock TransactionAssessmentEvent with random data.
   *
   * @return A mock TransactionAssessmentEvent object
   */
  public static TransactionAssessmentEvent createMockTransactionAssessmentEvent() {
    Account account = Account.newBuilder()
        .setBsb("123456")
        .setNumber("12345678")
        .build();

    // Create TransactionDetails
    TransactionDetails transactionDetails = TransactionDetails.newBuilder()
        .setCgid("CG" + UUID.randomUUID().toString().substring(0, 8))
        .setAccount(account)
        .build();

    // Create TransactionAmount
    TransactionAmount transactionAmount = TransactionAmount.newBuilder()
        .setCurrencyCode("AUD")
        .setValue(new BigDecimal("100.00"))
        .build();

    TransactionAssessmentEventData eventData = TransactionAssessmentEventData.newBuilder()
        .setTransactionAssessmentId(UUID.randomUUID().toString())
        .setTransactionAssessmentStatus(TransactionAssessmentStatus.COMPLETED)
        .setTransactionOutcome(TransactionOutcome.PROCEED)
        .setTransactionDetails(transactionDetails)
        .setTransactionType(TransactionType.PAYMENT)
        .setTransactionAmount(transactionAmount)
        .build();

    return TransactionAssessmentEvent.newBuilder()
        .setEventVersion("1.0")
        .setId(UUID.randomUUID().toString())
        .setType("TRANSACTION_ASSESSMENT")
        .setTime(Instant.now().toString())
        .setSubject("Transaction Assessment")
        .setSource("teller")
        .setProducer("APP-19279")
        .setCorrelationId(List.of(UUID.randomUUID().toString()))
        .setTraceId(UUID.randomUUID().toString())
        .setData(eventData)
        .build();
  }

  /**
   * Creates a mock EventKey with a random UUID.
   *
   * @return A mock EventKey object
   */
  public static EventKey createMockEventKey() {
    return EventKey.newBuilder().setEventKey(UUID.randomUUID().toString()).build();
  }
}
