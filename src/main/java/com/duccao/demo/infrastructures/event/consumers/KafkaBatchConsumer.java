package com.duccao.demo.infrastructures.event.consumers;

import com.duccao.demo.share.tracing.TraceMethodExecution;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public abstract class KafkaBatchConsumer<K, V> {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public void consumeRecords(ConsumerRecords<K, V> records, Acknowledgment acks) {
    var receivedTimestamp = Instant.now();
    var channel = getChannel();
    var minTs = Long.MAX_VALUE;
    var maxTs = Long.MIN_VALUE;
    var batchSize = CollectionUtils.size(records);

    var recordAndPartitionBuilder = new StringBuilder();
    for (var record : records) {
      minTs = Math.min(minTs, record.timestamp());
      maxTs = Math.max(maxTs, record.timestamp());
      recordAndPartitionBuilder
          .append(record.partition())
          .append(':')
          .append(extractCorrelationId(record));
    }

    var batchCorId = UUID.randomUUID().toString();
    var mdcMap = createLoggingMdcProperties(records, batchCorId, receivedTimestamp.toString(), channel);
    log.info(
        "message=Consume Records, receivedAt={}, type={}, batchSize={}, batchCorId={}, minTs={}, maxTs={}, correlationIds={}",
        receivedTimestamp, channel, batchSize, batchCorId, minTs, maxTs, recordAndPartitionBuilder);

    try {
      processRecords(records);
    } finally {
      log.debug("message = Acknowledging events batchCorId = {}, correlationId = {}", recordAndPartitionBuilder,
          batchCorId);
      acks.acknowledge();
    }
  }

  private Map<String, String> createLoggingMdcProperties(ConsumerRecords<K, V> records, String batchCorId,
                                                         String receivedTimestamp, String channel) {
    return Map.of(
        "batchCorId", batchCorId,
        "numOfRecords", String.valueOf(CollectionUtils.size(records)),
        "receivedTimestamp", receivedTimestamp,
        "channelId", channel
    );
  }

  @TraceMethodExecution
  protected abstract void processRecords(ConsumerRecords<K, V> records);

  protected abstract String extractCorrelationId(ConsumerRecord<K, V> record);

  protected abstract String getChannel();
}
