package com.duccao.demo.share.batches;

import java.util.List;

public record ChunkProcessingResult<T>(
    int totalEntities,
    int totalChunks,
    int successfulChunks,
    int failedChunks,
    List<T> successfulEntities,
    List<ChunkError<T>> errors,
    long processingTimeMs
) {
  public boolean hasErrors() {
    return failedChunks > 0 || !errors.isEmpty();
  }

  public boolean isCompleteSuccess() {
    return failedChunks == 0 && errors.isEmpty();
  }

  public double getSuccessRate() {
    return totalChunks > 0 ? (double) successfulChunks / totalChunks : 0.0;
  }

  public ChunkProcessingResult {
    if (totalEntities < 0) {
      throw new IllegalArgumentException("Total entities cannot be negative");
    }
    if (totalChunks < 0) {
      throw new IllegalArgumentException("Total chunks cannot be negative");
    }
    // Make defensive copies if needed
    successfulEntities = List.copyOf(successfulEntities);
    errors = List.copyOf(errors);
  }
}
