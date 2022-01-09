package org.zerobs.polla.test.utils;

import org.zerobs.polla.entities.db.Tag;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.utilities.Utils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;

public class DBInitializerUtil {
    private static final String PK_ATTRIBUTE_NAME = "pk";
    private static final String SK_ATTRIBUTE_NAME = "sk";
    private static final long RCU = 3;
    private static final long WCU = 3;

    private final DynamoDbClient dynamoDbClient;

    public DBInitializerUtil(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    //@PostConstruct
    public void initDB() throws InterruptedException {
        if (!dynamoDbClient.listTables().tableNames().contains(TABLE_NAME)) {
            CreateTableResponse createTableResponse = createTable(getGsiUsername(), getGsiTagName());
            while (!createTableResponse.tableDescription().tableStatus().equals(TableStatus.ACTIVE))
                //noinspection BusyWait
                Thread.sleep(5000);
        }

        var globalSecondaryIndexes = Utils.cleanList(dynamoDbClient.describeTable(
                DescribeTableRequest.builder().tableName(TABLE_NAME).build()).table().globalSecondaryIndexes());

        if (globalSecondaryIndexes.stream().noneMatch(gsi -> User.GSI_NAME_USERNAME.equals(gsi.indexName()))) {
            addGsiUsername();
            waitForIndexActive(User.GSI_NAME_USERNAME);
        }

        if (globalSecondaryIndexes.stream().noneMatch(gsi -> Tag.GSI_NAME_TAG_NAME.equals(gsi.indexName()))) {
            addGsiTagName();
            waitForIndexActive(Tag.GSI_NAME_TAG_NAME);
        }
    }

    private CreateTableResponse createTable(GlobalSecondaryIndex... globalSecondaryIndices) {
        return dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(KeySchemaElement.builder().attributeName(PK_ATTRIBUTE_NAME).keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName(SK_ATTRIBUTE_NAME).keyType(KeyType.RANGE).build())
                .attributeDefinitions(AttributeDefinition.builder().attributeName(PK_ATTRIBUTE_NAME).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(SK_ATTRIBUTE_NAME).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(User.PK_USERNAME_GSI).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(Tag.PK_TAG_NAME_GSI).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(Tag.SK_TAG_NAME_GSI).attributeType(ScalarAttributeType.S).build())
                .billingMode(BillingMode.PROVISIONED)
                .globalSecondaryIndexes(globalSecondaryIndices)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(RCU).writeCapacityUnits(WCU).build()).build());
    }

    private GlobalSecondaryIndex getGsiUsername() {
        return GlobalSecondaryIndex.builder()
                .indexName(User.GSI_NAME_USERNAME)
                .keySchema(KeySchemaElement.builder().attributeName(User.PK_USERNAME_GSI).keyType(KeyType.HASH).build())
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(RCU).writeCapacityUnits(WCU).build()).build();
    }

    private GlobalSecondaryIndex getGsiTagName() {
        return GlobalSecondaryIndex.builder()
                .indexName(Tag.GSI_NAME_TAG_NAME)
                .keySchema(KeySchemaElement.builder().attributeName(Tag.PK_TAG_NAME_GSI).keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName(Tag.SK_TAG_NAME_GSI).keyType(KeyType.RANGE).build())
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(RCU).writeCapacityUnits(WCU).build()).build();
    }

    private void addGsiUsername() {
        dynamoDbClient.updateTable(UpdateTableRequest.builder()
                .tableName(TABLE_NAME)
                .attributeDefinitions(AttributeDefinition.builder().attributeName(User.PK_USERNAME_GSI).attributeType(ScalarAttributeType.S).build())
                .globalSecondaryIndexUpdates(GlobalSecondaryIndexUpdate.builder()
                        .create(CreateGlobalSecondaryIndexAction.builder()
                                .indexName(User.GSI_NAME_USERNAME)
                                .keySchema(KeySchemaElement.builder().attributeName(User.PK_USERNAME_GSI).keyType(KeyType.HASH).build())
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(RCU).writeCapacityUnits(WCU).build()).build()).build()).build());
    }

    private void addGsiTagName() {
        dynamoDbClient.updateTable(UpdateTableRequest.builder()
                .tableName(TABLE_NAME)
                .attributeDefinitions(AttributeDefinition.builder().attributeName(Tag.PK_TAG_NAME_GSI).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(Tag.SK_TAG_NAME_GSI).attributeType(ScalarAttributeType.S).build())
                .globalSecondaryIndexUpdates(GlobalSecondaryIndexUpdate.builder()
                        .create(CreateGlobalSecondaryIndexAction.builder()
                                .indexName(Tag.GSI_NAME_TAG_NAME)
                                .keySchema(KeySchemaElement.builder().attributeName(Tag.PK_TAG_NAME_GSI).keyType(KeyType.HASH).build(),
                                        KeySchemaElement.builder().attributeName(Tag.SK_TAG_NAME_GSI).keyType(KeyType.RANGE).build())
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(RCU).writeCapacityUnits(WCU).build()).build()).build()).build());
    }

    private void waitForIndexActive(String indexName) throws InterruptedException {
        TableDescription tableDescription = dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(TABLE_NAME).build()).table();
        GlobalSecondaryIndexDescription gsiDescription = tableDescription.globalSecondaryIndexes().stream().filter(gsi -> indexName.equals(gsi.indexName()))
                .findAny().orElseThrow();
        while (!gsiDescription.indexStatus().equals(IndexStatus.ACTIVE))
            //noinspection BusyWait
            Thread.sleep(5000);
    }
}