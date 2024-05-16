package ro.axon.dot.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@Getter
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    private final BusinessErrorCode error;
    private final Map<String, Object> contextVariables;
}
