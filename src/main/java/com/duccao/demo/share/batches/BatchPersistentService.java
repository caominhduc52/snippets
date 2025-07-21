package com.duccao.demo.share.batches;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchPersistentService {

//  @Value("${consumer.detectionOutcome.dbBatchSize}")
  private int dbChunkSize = 1000;

  public <T> void persistInBatches(JpaRepository<T, ?> repository, List<T> entities) {
    persistInBatches(repository, entities, dbChunkSize);
  }

  public <T> void persistInBatches(JpaRepository<T, ?> repository, List<T> entities, int dbChunkSize) {
    if (isEmpty(entities) || dbChunkSize <= 0) {
      return;
    }

    for (int i = 0; i < entities.size(); i += dbChunkSize) {
      int end = Math.min(i + dbChunkSize, entities.size());
      List<T> chunk = entities.subList(i, end);
      repository.saveAll(chunk);
    }
  }

  public <T> ChunkProcessingResult<T> persistInBatchesWithResult(JpaRepository<T, ?> repository,
                                                                 List<T> entities, int dbChunkSize) {
    if (isEmpty(entities) || dbChunkSize <= 0) {
      return new ChunkProcessingResult<>(0, 0, 0, 0, List.of(),
          List.of(), 0L);
    }

    long startTime = System.currentTimeMillis();
    List<T> successfulEntities = new ArrayList<>();
    List<ChunkError<T>> errors = new ArrayList<>();
    int successfulChunks = 0;
    int chunkIndex = 0;

    for (int i = 0; i < entities.size(); i += dbChunkSize) {
      int end = Math.min(i + dbChunkSize, entities.size());
      List<T> chunk = entities.subList(i, end);

      try {
        List<T> savedEntities = repository.saveAll(chunk);
        successfulEntities.addAll(savedEntities);
        successfulChunks++;
      } catch (Exception e) {
        errors.add(new ChunkError<>(chunkIndex, i, end, chunk, e));
      }
      chunkIndex++;
    }

    long processingTime = System.currentTimeMillis() - startTime;
    int totalChunks = (int) Math.ceil((double) entities.size() / dbChunkSize);

    return new ChunkProcessingResult<>(
        entities.size(),
        totalChunks,
        successfulChunks,
        errors.size(),
        successfulEntities,
        errors,
        processingTime
    );
  }
}
