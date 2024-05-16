package ro.axon.dot.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;



  @RequiredArgsConstructor
  @Getter
  public enum LeaveRequestType {
    VACATION(0), MEDICAL(1);

    private final int priority;
  }

