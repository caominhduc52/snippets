package com.duccao.demo.infrastructures.publishers;

import com.duccao.avro.fltech.ms.teller.EventKey;
import com.duccao.avro.fltech.ms.teller.TransactionAssessmentEvent;
import com.duccao.demo.share.helpers.ErrorHelper;
import com.duccao.demo.share.helpers.TransactionAssessmentEventHelper;
import com.duccao.demo.share.tracing.TraceMethodExecutionTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAssessmentPublisher {

  @Value("${services.kafka.topic.frontline-assessment-topic}")
  private String transactionAssessmentTopic;

  private final KafkaTemplate<EventKey, TransactionAssessmentEvent> kafkaTemplate;

  @TraceMethodExecutionTime
  public void publishTransactionAssessmentEvent() {
    try {
      EventKey eventKey = TransactionAssessmentEventHelper.createMockEventKey();
      TransactionAssessmentEvent transactionAssessmentEvent =
          TransactionAssessmentEventHelper.createMockTransactionAssessmentEvent();

      CompletableFuture<SendResult<EventKey, TransactionAssessmentEvent>> future =
          kafkaTemplate.send(transactionAssessmentTopic, eventKey, transactionAssessmentEvent);
      future.whenComplete(onTransactionAssessmentEventCallback());
    } catch (Exception exception) {
      throw ErrorHelper.buildInternalException(exception, "Failed to published transaction assessment event");
    }
  }

  private BiConsumer<SendResult<EventKey, TransactionAssessmentEvent>, Throwable> onTransactionAssessmentEventCallback() {
    return (result, exception) -> {
      String correlationId = UUID.randomUUID().toString();
      if (Objects.nonNull(result)) {
        log.info("correlationId={}, status=SUCCESS, function={}, message=Operation completed successfully",
            correlationId, "TransactionAssessmentPublisher::onSuccess");
      } else if (Objects.nonNull(exception)) {
        log.error("correlationId={}, status=FAILURE, function={}, message=Operation failed with exception: {}",
            correlationId, "TransactionAssessmentPublisher::onFailure", exception.getMessage(), exception);
      } else {
        log.info("correlationId={}, status=UNKNOWN, function={}, message=Operation completed with null result and null exception",
            correlationId, "TransactionAssessmentPublisher::onUnknown");
      }
    };
  }
}
