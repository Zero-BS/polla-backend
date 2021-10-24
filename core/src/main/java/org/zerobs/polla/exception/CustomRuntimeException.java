package org.zerobs.polla.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomRuntimeException extends RuntimeException {
    private final RuntimeExceptionType runtimeExceptionType;
    private final transient Map<String, Object> variables;

    public CustomRuntimeException(RuntimeExceptionType runtimeExceptionType) {
        this.runtimeExceptionType = runtimeExceptionType;
        variables = null;
    }

    public CustomRuntimeException(RuntimeExceptionType runtimeExceptionType, Throwable e) {
        super(runtimeExceptionType.getMessageTextPropertyId(), e);
        this.runtimeExceptionType = runtimeExceptionType;
        variables = null;
    }
}