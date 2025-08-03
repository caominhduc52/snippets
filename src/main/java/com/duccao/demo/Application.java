package com.duccao.demo;

import com.duccao.starterkafka.anotations.DeliveryGuarantee;
import com.duccao.starterkafka.anotations.EnableKafkaAutoConfig;
import com.duccao.starterkafka.anotations.KafkaDelivery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafkaAutoConfig
@KafkaDelivery(guarantee = DeliveryGuarantee.EFFECTIVELY_ONCE)
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
