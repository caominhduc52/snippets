package component.com.duccao.demo;

import static org.assertj.core.api.Assertions.assertThat;

import annotations.ComponentTest;
import org.junit.jupiter.api.Test;

@ComponentTest
class TestControllerTest {

  @Test
  void duccao() {
    assertThat(true).isTrue();
  }
}
