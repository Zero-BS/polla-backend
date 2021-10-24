package org.zerobs.polla;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.http.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.zerobs.polla.entities.ExceptionResponseBody;
import org.zerobs.polla.entities.Gender;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.test.utils.DBInitializerUtil;
import org.zerobs.polla.test.utils.Util;

import static io.micronaut.http.HttpMethod.DELETE;
import static io.micronaut.http.HttpMethod.GET;
import static io.micronaut.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.zerobs.polla.exception.RuntimeExceptionType.*;

@TestMethodOrder(MethodOrderer.Random.class)
public class UserControllerTest {
    private static final String BASE_URL = "/v1/users";
    private static final Context lambdaContext = new MockLambdaContext();
    private static MicronautLambdaHandler handler;
    private static ApplicationContext applicationContext;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setupSpec() throws InterruptedException, ContainerInitializationException {
        handler = new MicronautLambdaHandler();
        applicationContext = handler.getApplicationContext();
        objectMapper = applicationContext.getBean(ObjectMapper.class);
        new DBInitializerUtil(applicationContext.getBean(DynamoDB.class),
                applicationContext.getBean(AmazonDynamoDB.class)).initDB();
    }

    @AfterAll
    public static void cleanupSpec() {
        applicationContext.close();
    }

    @Test
    void test_get_UserNotCreated_404() {
        assertEquals(OK.getCode(), getAwsProxyResponse(DELETE).getStatusCode());
        assertEquals(NOT_FOUND.getCode(), getAwsProxyResponse(GET).getStatusCode());
    }

    @Test
    void test_add_emptyBody_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getAwsProxyResponse(DELETE).getStatusCode());
        AwsProxyResponse awsProxyResponse = getPostAwsProxyResponse(null);
        assertEquals(BAD_REQUEST.getCode(), awsProxyResponse.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(awsProxyResponse.getBody(), ExceptionResponseBody.class);
        assertEquals("Please provide user details.", responseBody.getMessageText());
        assertEquals(EMPTY_USER.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_emptyUsername_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getAwsProxyResponse(DELETE).getStatusCode());
        AwsProxyResponse awsProxyResponse = getPostAwsProxyResponse(new User());
        assertEquals(BAD_REQUEST.getCode(), awsProxyResponse.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(awsProxyResponse.getBody(), ExceptionResponseBody.class);
        String messagePrefix = "Please provide a username. For your privacy, it is suggested that use a username that " +
                "can not be used to identify you. Try something like ";
        assertTrue(responseBody.getMessageText().startsWith(messagePrefix));
        assertTrue(StringUtils.isNotBlank(responseBody.getMessageText().substring(messagePrefix.length())));
        assertEquals(EMPTY_USERNAME.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_emptyYearOfBirth_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getAwsProxyResponse(DELETE).getStatusCode());
        User user = new User();
        user.setUsername("GreenDemogoblin_4678");
        user.setGender(Gender.FEMALE);
        AwsProxyResponse awsProxyResponse = getPostAwsProxyResponse(user);
        assertEquals(BAD_REQUEST.getCode(), awsProxyResponse.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(awsProxyResponse.getBody(), ExceptionResponseBody.class);
        assertEquals("Please enter your year of birth. This is essential for providing meaningful analytics on polls. " +
                "Your privacy is our top priority. We do not use any information to identify you.", responseBody.getMessageText());
        assertEquals(EMPTY_YEAR_OF_BIRTH.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_necessaryFieldsGiven_201() throws JsonProcessingException {
        assertEquals(OK.getCode(), getAwsProxyResponse(DELETE).getStatusCode());
        User user = new User();
        user.setUsername("GreenDemogoblin_4678");
        user.setGender(Gender.FEMALE);
        user.setYearOfBirth(1995);
        AwsProxyResponse awsProxyResponse = getPostAwsProxyResponse(user);
        assertEquals(CREATED.getCode(), awsProxyResponse.getStatusCode());
    }

    private AwsProxyResponse getPostAwsProxyResponse(Object body) throws JsonProcessingException {
        String bodyAsString = body == null ? null : objectMapper.writeValueAsString(body);
        return getProxyResponse(Util.getAwsProxyRequest(BASE_URL, HttpMethod.POST, bodyAsString));
    }

    private AwsProxyResponse getAwsProxyResponse(HttpMethod httpMethod) {
        return getProxyResponse(Util.getAwsProxyRequest(BASE_URL, httpMethod));
    }

    private AwsProxyResponse getProxyResponse(AwsProxyRequest awsProxyRequest) {
        return handler.handleRequest(awsProxyRequest, lambdaContext);
    }
}
