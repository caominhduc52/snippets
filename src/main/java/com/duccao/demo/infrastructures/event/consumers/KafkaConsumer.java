package com.duccao.demo.infrastructures.event.consumers;

import com.duccao.demo.share.tracing.TraceMethodExecution;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;

public abstract class KafkaConsumer<K, V> {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public void consumeRecord(ConsumerRecord<K, V> record, Acknowledgment acks) {
    var receivedTimestamp = Instant.now();
    var channel = getChannel();
    var minTs = Long.MAX_VALUE;
    var maxTs = Long.MIN_VALUE;
    String correlationId = extractCorrelationId(record);

    try {
      log.info("message=Consume Records, receivedAt={}, type={}, minTs={}, maxTs={}, correlationIds={}",
          receivedTimestamp, channel, minTs, maxTs, correlationId);
      processRecord(record);
    } finally {
      log.debug("message = Acknowledging events, correlationId = {}", correlationId);
      acks.acknowledge();
    }
  }

  @TraceMethodExecution
  protected abstract void processRecord(ConsumerRecord<K, V> record);

  protected abstract String extractCorrelationId(ConsumerRecord<K, V> record);

  protected abstract String getChannel();
}
