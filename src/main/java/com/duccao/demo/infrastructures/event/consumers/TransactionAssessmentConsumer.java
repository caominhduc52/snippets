package com.duccao.demo.infrastructures.event.consumers;

import com.duccao.avro.teller.EventKey;
import com.duccao.avro.teller.TransactionAssessmentEvent;
import com.duccao.starterkafka.configurations.constants.ListenerContainerFactoryConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(
    value = {"toggle.kafka.enabled", "toggle.transactionAssessment.enabled"},
    havingValue = "true")
public class TransactionAssessmentConsumer {

  private static final String TRANSACTION_ASSESSMENT_TOPIC = "TRANSACTION_ASSESSMENT_TOPIC";

  @KafkaListener(
      topics = "${services.kafka.topic.frontline-assessment-topic}",
      groupId = "${services.kafka.topic.frontline-assessment-topic}",
      concurrency = "1",
      containerFactory = ListenerContainerFactoryConstant.AVRO_SPECIFIC)
  protected void processRecords(ConsumerRecord<EventKey, TransactionAssessmentEvent> record) {
    log.info("message=\"Handle transaction assessment record\"");
  }
}
