package org.zerobs.polla;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
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
import org.zerobs.polla.entities.Principal;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.test.utils.DBInitializerUtil;
import org.zerobs.polla.test.utils.Util;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.Year;
import java.time.ZoneId;

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
        new DBInitializerUtil(applicationContext.getBean(DynamoDbClient.class)).initDB();
    }

    @AfterAll
    public static void cleanupSpec() {
        applicationContext.close();
    }

    @Test
    void test_get_UserNotCreated_404() {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        assertEquals(NOT_FOUND.getCode(), getResponse(GET).getStatusCode());
    }

    @Test
    void test_add_emptyBody_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        AwsProxyResponse response = getResponse((User) null);
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        assertEquals("Please provide user details.", responseBody.getMessageText());
        assertEquals(EMPTY_USER.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_emptyUsername_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        AwsProxyResponse response = getResponse(new User());
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        String messagePrefix = "Please provide a username. For your privacy, it is suggested that use a username that " +
                "can not be used to identify you. Try something like ";
        assertTrue(responseBody.getMessageText().startsWith(messagePrefix));
        assertTrue(StringUtils.isNotBlank(responseBody.getMessageText().substring(messagePrefix.length())));
        assertEquals(EMPTY_USERNAME.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_emptyYearOfBirth_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        User user = new User();
        user.setUsername("GreenDemogoblin_4678");
        user.setGender(Gender.FEMALE);
        AwsProxyResponse response = getResponse(user);
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        assertEquals("Please enter your year of birth. This is essential for providing meaningful analytics on polls. " +
                "Your privacy is our top priority. We do not use any information to identify you.", responseBody.getMessageText());
        assertEquals(EMPTY_YEAR_OF_BIRTH.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_necessaryFieldsGiven_201() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        User user = getUser();

        AwsProxyRequest request = getRequest(user);
        assertEquals(CREATED.getCode(), getResponse(request).getStatusCode());

        AwsProxyResponse response = getResponse(GET);
        assertEquals(OK.getCode(), response.getStatusCode());
        User savedUser = objectMapper.readValue(response.getBody(), User.class);
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getGender(), savedUser.getGender());
        assertEquals(user.getYearOfBirth(), savedUser.getYearOfBirth());

        Principal principal = new Principal(request.getRequestContext().getAuthorizer());
        assertEquals(principal.getEmail(), savedUser.getEmail());
        assertEquals(principal.getEmailVerified(), savedUser.getEmailVerified());
        assertEquals(principal.getLocale(), savedUser.getLocale());
        assertEquals(principal.getAuthority() + "#" + principal.getSubject(), savedUser.getId());
    }

    @Test
    void test_add_existingUser_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());

        User user = getUser();
        assertEquals(CREATED.getCode(), getResponse(user).getStatusCode());

        AwsProxyResponse response = getResponse(user);
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        assertEquals("User already exists.", responseBody.getMessageText());
        assertEquals(EXISTING_USER.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_existingUsername_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());

        User user = getUser();
        assertEquals(CREATED.getCode(), getResponse(user).getStatusCode());

        AwsProxyRequest request = getRequest(user);
        request.getRequestContext().getAuthorizer().setPrincipalId("987654321");
        AwsProxyResponse response = getResponse(request);
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        String messagePrefix = "This username is taken. Suggestion: try something like ";
        assertTrue(responseBody.getMessageText().startsWith(messagePrefix));
        assertTrue(StringUtils.isNotBlank(responseBody.getMessageText().substring(messagePrefix.length())));
        assertEquals(EXISTING_USERNAME.getInternalCode(), responseBody.getInternalCode());
    }

    @Test
    void test_add_tooYoungUser_400() throws JsonProcessingException {
        assertEquals(OK.getCode(), getResponse(DELETE).getStatusCode());
        User user = new User();
        user.setUsername("GreenDemogoblin_4678");
        user.setGender(Gender.FEMALE);
        user.setYearOfBirth(Year.now(ZoneId.of("UTC")).getValue() - 2);
        AwsProxyResponse response = getResponse(user);
        assertEquals(BAD_REQUEST.getCode(), response.getStatusCode());
        ExceptionResponseBody responseBody = objectMapper.readValue(response.getBody(), ExceptionResponseBody.class);
        assertEquals("You are too young.", responseBody.getMessageText());
        assertEquals(TOO_YOUNG_USER.getInternalCode(), responseBody.getInternalCode());
    }

    private User getUser() {
        User user = new User();
        user.setUsername("GreenDemogoblin_4678");
        user.setGender(Gender.FEMALE);
        user.setYearOfBirth(1995);
        return user;
    }

    private AwsProxyRequest getRequest(User body) throws JsonProcessingException {
        String bodyAsString = body == null ? null : objectMapper.writeValueAsString(body);
        return Util.getRequest(BASE_URL, HttpMethod.POST, bodyAsString);
    }

    private AwsProxyResponse getResponse(User body) throws JsonProcessingException {
        return getResponse(getRequest(body));
    }

    private AwsProxyResponse getResponse(HttpMethod httpMethod) {
        return getResponse(Util.getRequest(BASE_URL, httpMethod));
    }

    private AwsProxyResponse getResponse(AwsProxyRequest request) {
        return handler.handleRequest(request, lambdaContext);
    }
}
