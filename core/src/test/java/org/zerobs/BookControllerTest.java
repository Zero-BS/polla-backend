package org.zerobs;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.zerobs.entities.Principal;

public class BookControllerTest {

    private static CustomLambdaHandler handler;
    private static final Context lambdaContext = new MockLambdaContext();
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setupSpec() {
        try {
            handler = new CustomLambdaHandler();
            objectMapper = handler.getApplicationContext().getBean(ObjectMapper.class);

        } catch (ContainerInitializationException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    void testSaveBook() throws JsonProcessingException {
        Book book = new Book();
        book.setName("Building Microservices");
        String json = objectMapper.writeValueAsString(book);
        Principal principal = getPrincipal();

        AwsProxyRequest request = new AwsProxyRequestBuilder("/book", HttpMethod.POST.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(json)
                .authorizerPrincipal(objectMapper.writeValueAsString(principal))
                .build();
        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        BookSaved bookSaved = objectMapper.readValue(response.getBody(), BookSaved.class);
        Assertions.assertEquals(principal.getName(), bookSaved.getName());
        Assertions.assertNotNull(bookSaved.getIsbn());
    }

    private Principal getPrincipal() {
        Principal principal = new Principal();
        principal.setIss("https://accounts.google.com");
        principal.setSub("2345678");
        principal.setEmail("abc@xyz.com");
        principal.setEmailVerified(true);
        principal.setGivenName("Green");
        principal.setFamilyName("Demogoblin");
        principal.setName(principal.getGivenName() + " " + principal.getFamilyName());
        principal.setLocale("en");
        principal.setPicture("https://lh3.googleusercontent.com/a-/sdfghjk3456789");
        return principal;
    }
}
