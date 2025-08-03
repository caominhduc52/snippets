package com.duccao.demo.infrastructures.configs;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FeatureToggleContributor implements InfoContributor {

  private final Environment environment;

  @Override
  public void contribute(Info.Builder builder) {
    builder.withDetail("feature_toggles", generateFeatureToggleInfo());
  }

  private Map<String, Object> generateFeatureToggleInfo() {
    Map<String, Object> toggleInfo = new HashMap<>();
    toggleInfo.put("kafka", retrieveToggleStatus("toggle.kafka.enabled"));
    toggleInfo.put("transactionAssessment", retrieveToggleStatus("toggle.transactionAssessment.enabled"));
    return toggleInfo;
  }

  private String retrieveToggleStatus(String property) {
    Boolean toggle = environment.getProperty(property, Boolean.class);
    return BooleanUtils.toString(toggle, "On", "Off", "?");
  }
}
