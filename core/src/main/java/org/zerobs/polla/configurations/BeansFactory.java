package org.zerobs.polla.configurations;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Singleton;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Locale;

@Factory
public class BeansFactory {
    @ReflectiveAccess //required when using @Value on private fields and building with graalvm native
    @Value("${use.local.db:true}")
    private boolean useLocalDb;

    @ReflectiveAccess
    @Value("${default.locale}")
    private String defaultLocaleTag;

    @Primary
    @Singleton
    public DynamoDbClient dynamoDbClient() {
        if (useLocalDb) return DynamoDbClient.builder().endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1).credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy-key", "dummy-secret"))).build();
        else return DynamoDbClient.create();
    }

    @Singleton
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @Singleton
    public Locale defaultLocale() {
        return Locale.forLanguageTag(defaultLocaleTag);
    }

    @Singleton
    public MessageSource messageSource() {
        return new ResourceBundleMessageSource("messages", defaultLocale());
    }
}