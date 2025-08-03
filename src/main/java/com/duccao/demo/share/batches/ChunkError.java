package com.duccao.demo.share.batches;

import java.util.List;

public record ChunkError<T>(int chunkIndex, int startIndex, int endIndex, List<T> failedEntities, Exception exception) {
  public ChunkError {
    if (chunkIndex < 0) {
      throw new IllegalArgumentException("Chunk index cannot be negative");
    }

    if (startIndex < 0 || endIndex < startIndex) {
      throw new IllegalArgumentException("Invalid index range");
    }
    failedEntities = List.copyOf(failedEntities);
  }

  public int getChunkSize() {
    return endIndex - startIndex;
  }

  public String getErrorMessage() {
    return exception != null ? exception.getMessage() : "Unknown error";
  }
}
