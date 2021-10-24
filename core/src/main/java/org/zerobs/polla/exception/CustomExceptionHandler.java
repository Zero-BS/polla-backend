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
@Requires(classes = {Exception.class, ExceptionHandler.class})
@Slf4j
public class CustomExceptionHandler implements ExceptionHandler<Exception, HttpResponse<?>> {
    @Inject
    private MessageSource messageSource;
    @Inject
    private Locale defaultLocale;

    @Override
    public HttpResponse<?> handle(HttpRequest request, Exception e) {
        Locale locale = (Locale) request.getLocale().orElse(defaultLocale);
        
        String messageTitle = messageSource.getMessage(CustomException.MESSAGE_TITLE_PROPERTY_ID, 
                MessageContext.of(locale)).orElse(null);
        String messageText = messageSource.getMessage(CustomException.MESSAGE_TEXT_PROPERTY_ID, 
                MessageContext.of(locale)).orElse(null);

        log.error("Exception message: {}", e.getMessage(), e);

        MutableHttpResponse<Object> response = HttpResponse.status(CustomException.HTTP_STATUS);
        response.body(new ExceptionResponseBody(messageTitle, messageText, CustomException.INTERNAL_CODE));
        return response;
    }
}