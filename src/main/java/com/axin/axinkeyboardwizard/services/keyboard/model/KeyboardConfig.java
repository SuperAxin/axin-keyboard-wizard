package com.axin.axinkeyboardwizard.services.keyboard.model;

import lombok.Data;

@Data
public class KeyboardConfig {
  int keyCode;
  long intervalMillis;

  public KeyboardConfig(int keyCode, long intervalMillis) {
    this.keyCode = keyCode;
    this.intervalMillis = intervalMillis;
  }
}
