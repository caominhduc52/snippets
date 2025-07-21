package com.duccao.demo.controllers;

import com.duccao.demo.infrastructures.publishers.TransactionAssessmentPublisher;
import com.duccao.demo.share.logging.CallableService;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

  private final TransactionAssessmentPublisher transactionAssessmentPublisher;
  private final CallableService callableService;


  @GetMapping
  public Callable<ResponseEntity<Void>> test() {
    MDC.put("correlationId", "tao ne may");
    return callableService.wrap(() -> {
      transactionAssessmentPublisher.publishTransactionAssessmentEvent();
      return ResponseEntity.noContent().build();
    });
  }
}
