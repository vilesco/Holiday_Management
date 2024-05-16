package ro.axon.dot.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LeaveRequestStatus {
  PENDING(0), APPROVED(1), REJECTED(2);

  private final int priority;
}
