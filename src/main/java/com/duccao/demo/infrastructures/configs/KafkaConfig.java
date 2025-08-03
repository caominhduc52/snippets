package com.duccao.demo.infrastructures.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.AvroRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Optional;

import static java.util.Objects.isNull;

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
      ConcurrentKafkaListenerContainerFactory<K, V> defaultConcurrentKafkaListenerContainerFactory) {
    return defaultConcurrentKafkaListenerContainerFactory;
  }

  @Bean("kafkaBatchAvroSpecificListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaBatchListenerContainerFactory(
      ConcurrentKafkaListenerContainerFactoryConfigurer factoryConfigurer,
      @Qualifier("avroSpecificConsumerFactory") ConsumerFactory<Object, Object> kafkaConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factoryConfigurer.configure(factory, kafkaConsumerFactory);
    factory.setBatchListener(true);
    factory.setCommonErrorHandler(defaultErrorHandler());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    return factory;
  }

  @Bean
  public DefaultErrorHandler defaultErrorHandler() {
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(getConsumerRecordRecoverer(), getBackOff());
    errorHandler.setCommitRecovered(true);
    return errorHandler;
  }

  private ConsumerRecordRecoverer getConsumerRecordRecoverer() {
    return (consumerRecord, exception) -> logError(exception, consumerRecord, retryMaximumAttempts);
  }

  private FixedBackOff getBackOff() {
    return new FixedBackOff(retryInterval, retryMaximumAttempts);
  }

  private void logError(Exception exception, ConsumerRecord<?, ?> consumerRecord, long maxAttempts) {
    if (isInCompatibleAvroSchemaException(exception)) {
      log.error(
          "message=\"Error occurred when processing event due to the existing Avro schema change\", recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          consumerRecord.topic(),
          consumerRecord.partition(),
          consumerRecord.offset(),
          consumerRecord.key(),
          consumerRecord.serializedValueSize(),
          exception);
    } else if (exception instanceof DeserializationException) {
      log.error(
          "message=\"Could not deserialize event\", recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          consumerRecord.topic(),
          consumerRecord.partition(),
          consumerRecord.offset(),
          consumerRecord.key(),
          consumerRecord.serializedValueSize(),
          exception);
    } else {
      log.error(
          "message=\"Exhausted retries in kafka consumer\", retryCount=\"{}\",recordTopic=\"{}\", recordPartition=\"{}\", recordOffset=\"{}\", recordKey=\"{}\", recordValueSize=\"{}\"",
          maxAttempts,
          consumerRecord.topic(),
          consumerRecord.partition(),
          consumerRecord.offset(),
          consumerRecord.key(),
          consumerRecord.serializedValueSize(),
          exception);
    }
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
