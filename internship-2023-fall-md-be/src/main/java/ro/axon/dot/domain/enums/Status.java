package ro.axon.dot.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    ACTIVE(0), INACTIVE(1);

    private final int priority;
}
