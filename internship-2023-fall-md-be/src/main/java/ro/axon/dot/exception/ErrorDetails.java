package ro.axon.dot.exception;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetails {
    private final String message;
    private final String errorCode;
    private final Map<String, Object> contextVariables;
}
