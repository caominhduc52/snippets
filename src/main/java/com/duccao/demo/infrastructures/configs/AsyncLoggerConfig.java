package com.duccao.demo.infrastructures.configs;

import com.duccao.demo.share.logging.CallableService;
import com.duccao.demo.share.logging.MdcAwareCallableService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncLoggerConfig {

  @Bean
  public CallableService callableService() {
    return new MdcAwareCallableService();
  }
}
