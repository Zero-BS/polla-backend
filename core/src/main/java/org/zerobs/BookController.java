package org.zerobs;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.extern.slf4j.Slf4j;
import org.zerobs.entities.Principal;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.UUID;

@Controller
@Slf4j
public class BookController {
    @Inject
    Principal principal;

    @Post("book")
    public BookSaved save(@Valid @Body Book book, Context context, AwsProxyRequest awsProxyRequest) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        log.error(objectMapper.writeValueAsString(context));
        log.error(objectMapper.writeValueAsString(awsProxyRequest));
        BookSaved bookSaved = new BookSaved();
        bookSaved.setName(principal.getName());
        bookSaved.setIsbn(UUID.randomUUID().toString());
        return bookSaved;
    }
}
