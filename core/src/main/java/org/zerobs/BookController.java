package org.zerobs;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@Slf4j
public class BookController {
    @Post("book")
    public BookSaved save(@Valid @Body Book book, AwsProxyRequest awsProxyRequest) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        log.error(objectMapper.writeValueAsString(awsProxyRequest));
        log.error(awsProxyRequest.getRequestContext().getAuthorizer().getContextValue("iss"));
        log.error(awsProxyRequest.getRequestContext().getAuthorizer().getContextValue("family_name"));
        log.error(awsProxyRequest.getRequestContext().getAuthorizer().getContextValue("picture"));
        BookSaved bookSaved = new BookSaved();
        bookSaved.setIsbn(UUID.randomUUID().toString());
        return bookSaved;
    }
}