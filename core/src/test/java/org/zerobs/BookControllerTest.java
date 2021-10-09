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

    @Test
    void testSaveBook() throws JsonProcessingException {
        Book book = new Book();
        book.setName("Building Microservices");
        String json = objectMapper.writeValueAsString(book);
        AwsProxyRequest request = new AwsProxyRequestBuilder("/book", HttpMethod.POST.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(json)
                .authorizerPrincipal("{\"iss\":\"https://accounts.google.com\",\"azp\":\"1234567azp\",\"aud\":\"2345678aud\",\"sub\":\"123456789\",\"email\":\"abc@xyz.com\",\"email_verified\":true,\"name\":\"Green Demogoblin\",\"picture\":\"https://lh3.googleusercontent.com/a-/sdfghjk3456789\",\"given_name\":\"Green\",\"family_name\":\"Demogoblin\",\"locale\":\"en\",\"iat\":1633615166,\"exp\":1633618766}")
                .build();
        AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
        Assertions.assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        BookSaved bookSaved = objectMapper.readValue(response.getBody(), BookSaved.class);
        Assertions.assertEquals(bookSaved.getName(), book.getName());
        Assertions.assertNotNull(bookSaved.getIsbn());
    }
}
