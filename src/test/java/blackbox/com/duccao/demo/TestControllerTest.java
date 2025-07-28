package blackbox.com.duccao.demo;

import static org.assertj.core.api.Assertions.assertThat;

import annotations.BlackboxTest;
import org.junit.jupiter.api.Test;

@BlackboxTest
public class TestControllerTest {

  @Test
  void duccap() {
    assertThat(true).isTrue();
  }
}
