package org.zerobs.polla.exception;

import io.micronaut.context.MessageSource;
import io.micronaut.context.MessageSource.MessageContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.zerobs.polla.entities.ExceptionResponseBody;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Locale;

@Produces
@Singleton
@Requires(classes = {CustomRuntimeException.class, ExceptionHandler.class})
@Slf4j
public class CustomRuntimeExceptionHandler implements ExceptionHandler<CustomRuntimeException, HttpResponse<?>> {
    @Inject
    private MessageSource messageSource;
    @Inject
    private Locale defaultLocale;

    @Override
    public HttpResponse<?> handle(HttpRequest request, CustomRuntimeException e) {
        Locale locale = (Locale) request.getLocale().orElse(defaultLocale);

        String messageText = messageSource.getMessage(e.getRuntimeExceptionType().getMessageTextPropertyId(),
                MessageContext.of(locale, e.getVariables())).orElse(null);

        //keep minimal logging, enable by changing to error level only if needed
        log.info("CustomRuntimeException messageId: {}, internalCode: {}, httpStatusCode: {}",
                e.getRuntimeExceptionType().getMessageTextPropertyId(), e.getRuntimeExceptionType().getInternalCode(),
                e.getRuntimeExceptionType().getHttpStatus(), e);

        MutableHttpResponse<Object> response = HttpResponse.status(e.getRuntimeExceptionType().getHttpStatus());
        response.body(new ExceptionResponseBody(null, messageText, e.getRuntimeExceptionType().getInternalCode()));
        return response;
    }
}