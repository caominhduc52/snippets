package com.duccao.demo.share.batches;

import jakarta.validation.constraints.Positive;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class for splitting lists into batches/chunks of a specified size.
 */
@UtilityClass
public class Chunks {

  /**
   * Splits a list into batches of the specified chunkSize.
   *
   * @param <T>       the type of elements in the source list
   * @param source    the source list to be split into batches
   * @param chunkSize the maximum size of each batch
   * @return a stream of lists, each containing at most 'chunkSize' elements from the source list
   * @throws NullPointerException     if source is null
   * @throws IllegalArgumentException if chunkSize is not positive
   */
  public static <T> Stream<List<T>> ofSize(List<T> source, @Positive int chunkSize) {
    if (CollectionUtils.isEmpty(source)) {
      return Stream.empty();
    }

    int totalChunks = (source.size() + chunkSize - 1) / chunkSize;

    return IntStream.range(0, totalChunks)
        .mapToObj(
            chunkIndex -> {
              int start = chunkIndex * chunkSize;
              int end = Math.min(start + chunkSize, source.size());
              return source.subList(start, end);
            });
  }
}
