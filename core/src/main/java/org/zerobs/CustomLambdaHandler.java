package org.zerobs;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextProvider;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.proxy.MicronautLambdaContainerHandler;
import lombok.extern.slf4j.Slf4j;
import org.zerobs.entities.Principal;

import java.io.Closeable;

/**
 * Replica of io.micronaut.function.aws.proxy.MicronautLambdaHandler
 * with custom logic to set {@link Principal}
 * AWS {@link RequestHandler} for {@link AwsProxyRequest} and {@link AwsProxyResponse}.
 */
@Introspected
@Slf4j
public class CustomLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse>, ApplicationContextProvider, Closeable {
    protected final MicronautLambdaContainerHandler handler;

    /**
     * Constructor.
     *
     * @throws ContainerInitializationException thrown intializing {@link io.micronaut.function.aws.proxy.MicronautLambdaHandler}
     */
    public CustomLambdaHandler() throws ContainerInitializationException {
        this.handler = new MicronautLambdaContainerHandler();

    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest input, Context context) {
        try {
            getApplicationContext().getBean(Principal.class).cloneAndSet(new ObjectMapper()
                    .readValue(input.getRequestContext().getAuthorizer().getPrincipalId(), Principal.class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return handler.proxy(input, context);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.handler.getApplicationContext();
    }

    @Override
    public void close() {
        this.getApplicationContext().close();
    }
}
