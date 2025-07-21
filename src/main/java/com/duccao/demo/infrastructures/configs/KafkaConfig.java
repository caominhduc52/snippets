package com.duccao.demo.infrastructures.configs;

import static java.util.Objects.isNull;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.AvroRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConfig {

  @Value("${services.kafka.consumer.retry.interval}")
  private long retryInterval;

  @Value("${services.kafka.consumer.retry.max-attempts}")
  private long retryMaximumAttempts;

  @Bean
  public <K, V> ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory(
      @Qualifier("kafkaAvroSpecificListenerContainerFactory")
      ConcurrentKafkaListenerContainerFactory<K, V> defaultConcurrentKafkaListenerContainerFactory
  ) {
    defaultConcurrentKafkaListenerContainerFactory.setCommonErrorHandler(defaultErrorHandler());
    return defaultConcurrentKafkaListenerContainerFactory;
  }

  private DefaultErrorHandler defaultErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        (consumerRecord, exception) -> logError(exception, consumerRecord, retryMaximumAttempts),
        new FixedBackOff(retryInterval, retryMaximumAttempts)
    );
    errorHandler.setCommitRecovered(true);
    return errorHandler;
  }

  private void logError(Exception exception, ConsumerRecord<?, ?> consumerRecord, long maxAttempts) {
    if (isInCompatibleAvroSchemaException(exception)) {
      log.error(
          "message=\"Error occurred when processing event due to the existing Avro schema change\", recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key(),
          consumerRecord.serializedValueSize(), exception
      );
    } else if (exception instanceof DeserializationException) {
      log.error(
          "message=\"Could not deserialize event\", recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key(),
          consumerRecord.serializedValueSize(), exception
      );
    } else {
      log.error(
          "message=\"Exhausted retries in kafka consumer\", retryCount=\"{}\",recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          maxAttempts, consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(),
          consumerRecord.key(),
          consumerRecord.serializedValueSize(), exception
      );
    }
    // TODO: remove corrId
    // MDC.remove(RequestHeader.CORRELATION_ID.key());
  }

  private String getClassNameValue(ConsumerRecord<?, ?> consumerRecord) {
    return Optional.ofNullable(consumerRecord)
        .map(record -> record.getClass().getCanonicalName())
        .orElse(StringUtils.EMPTY);
  }

  private boolean isInCompatibleAvroSchemaException(Throwable thrownException) {
    if (thrownException instanceof AvroRuntimeException) {
      return true;
    } else if (isNull(thrownException)) {
      return false;
    }
    return isInCompatibleAvroSchemaException(thrownException.getCause());
  }
}
