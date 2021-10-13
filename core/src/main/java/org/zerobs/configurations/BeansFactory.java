package org.zerobs.configurations;

//import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
//import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import org.zerobs.entities.Principal;

import javax.inject.Singleton;

@Factory
public class BeansFactory {
//    @Value("${use.local.db:false}")
//    private boolean useLocalDb;
//    @Value("${aws.default.region}")
//    private Regions region;

    @Singleton
    public Principal principal() {
        return new Principal();
    }

//    @Singleton
//    public AmazonDynamoDB amazonDynamoDB() {
//        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
//                .withCredentials(new DefaultAWSCredentialsProviderChain());
//        if (useLocalDb)
//            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", region.getName()));
//        else
//            builder.withRegion(region);
//        return builder.build();
//    }
//
//    @Singleton
//    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB client) {
//        return new DynamoDBMapper(client);
//    }
//
//    @Singleton
//    public DynamoDB dynamoDB(AmazonDynamoDB client) {
//        return new DynamoDB(client);
//    }
}