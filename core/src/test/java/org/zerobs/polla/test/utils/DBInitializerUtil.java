package org.zerobs.polla.test.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.*;
import org.zerobs.polla.entities.db.Tag;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.utilities.Utils;

import java.util.stream.StreamSupport;

import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;

public class DBInitializerUtil {
    private static final String PK_ATTRIBUTE_NAME = "pk";
    private static final String SK_ATTRIBUTE_NAME = "sk";
    private static final long RCU = 3;
    private static final long WCU = 3;

    private final DynamoDB dynamoDB;
    private final AmazonDynamoDB amazonDynamoDB;

    public DBInitializerUtil(DynamoDB dynamoDB, AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDB = dynamoDB;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    //@PostConstruct
    public void initDB() throws InterruptedException {
        TableCollection<ListTablesResult> tables = dynamoDB.listTables();
        var pollaTable = StreamSupport.stream(tables.spliterator(), false)
                .filter(table -> TABLE_NAME.equals(table.getTableName())).findAny().orElse(null);

        if (pollaTable == null) {
            pollaTable = createTable(getGsiUsername(), getGsiTagName());
            pollaTable.waitForActive();
        }

        var globalSecondaryIndexes = Utils.cleanList(pollaTable.describe().getGlobalSecondaryIndexes());

        if (globalSecondaryIndexes.stream().noneMatch(gsi -> User.GSI_NAME_USERNAME.equals(gsi.getIndexName()))) {
            addGsiUsername();
            waitForIndexActive(pollaTable, User.GSI_NAME_USERNAME);
        }

        if (globalSecondaryIndexes.stream().noneMatch(gsi -> Tag.GSI_NAME_TAG_NAME.equals(gsi.getIndexName()))) {
            addGsiTagName();
            waitForIndexActive(pollaTable, Tag.GSI_NAME_TAG_NAME);
        }
    }

    private Table createTable(GlobalSecondaryIndex... globalSecondaryIndices) {
        return dynamoDB.createTable(new CreateTableRequest()
                .withTableName(TABLE_NAME)
                .withKeySchema(new KeySchemaElement(PK_ATTRIBUTE_NAME, KeyType.HASH),
                        new KeySchemaElement(SK_ATTRIBUTE_NAME, KeyType.RANGE))
                .withAttributeDefinitions(new AttributeDefinition(PK_ATTRIBUTE_NAME, ScalarAttributeType.S),
                        new AttributeDefinition(SK_ATTRIBUTE_NAME, ScalarAttributeType.S),
                        new AttributeDefinition(User.PK_USERNAME_GSI, ScalarAttributeType.S),
                        new AttributeDefinition(Tag.PK_TAG_NAME_GSI, ScalarAttributeType.S),
                        new AttributeDefinition(Tag.SK_TAG_NAME_GSI, ScalarAttributeType.S))
                .withBillingMode(BillingMode.PROVISIONED)
                .withGlobalSecondaryIndexes(globalSecondaryIndices)
                .withProvisionedThroughput(new ProvisionedThroughput(RCU, WCU)));
    }

    private GlobalSecondaryIndex getGsiUsername() {
        return new GlobalSecondaryIndex()
                .withIndexName(User.GSI_NAME_USERNAME)
                .withKeySchema(new KeySchemaElement(User.PK_USERNAME_GSI, KeyType.HASH))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                .withProvisionedThroughput(new ProvisionedThroughput(RCU, WCU));
    }

    private GlobalSecondaryIndex getGsiTagName() {
        return new GlobalSecondaryIndex()
                .withIndexName(Tag.GSI_NAME_TAG_NAME)
                .withKeySchema(new KeySchemaElement(Tag.PK_TAG_NAME_GSI, KeyType.HASH),
                        new KeySchemaElement(Tag.SK_TAG_NAME_GSI, KeyType.RANGE))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                .withProvisionedThroughput(new ProvisionedThroughput(RCU, WCU));
    }

    private void addGsiUsername() {
        amazonDynamoDB.updateTable(new UpdateTableRequest()
                .withTableName(TABLE_NAME)
                .withAttributeDefinitions(new AttributeDefinition(User.PK_USERNAME_GSI, ScalarAttributeType.S))
                .withGlobalSecondaryIndexUpdates(new GlobalSecondaryIndexUpdate()
                        .withCreate(new CreateGlobalSecondaryIndexAction()
                                .withIndexName(User.GSI_NAME_USERNAME)
                                .withKeySchema(new KeySchemaElement(User.PK_USERNAME_GSI, KeyType.HASH))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput(RCU, WCU)))));
    }

    private void addGsiTagName() {
        amazonDynamoDB.updateTable(new UpdateTableRequest()
                .withTableName(TABLE_NAME)
                .withAttributeDefinitions(new AttributeDefinition(Tag.PK_TAG_NAME_GSI, ScalarAttributeType.S),
                        new AttributeDefinition(Tag.SK_TAG_NAME_GSI, ScalarAttributeType.S))
                .withGlobalSecondaryIndexUpdates(new GlobalSecondaryIndexUpdate()
                        .withCreate(new CreateGlobalSecondaryIndexAction()
                                .withIndexName(Tag.GSI_NAME_TAG_NAME)
                                .withKeySchema(new KeySchemaElement(Tag.PK_TAG_NAME_GSI, KeyType.HASH),
                                        new KeySchemaElement(Tag.SK_TAG_NAME_GSI, KeyType.RANGE))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput(RCU, WCU)))));
    }

    private void waitForIndexActive(Table table, String indexName) throws InterruptedException {
        while (!table.describe().getGlobalSecondaryIndexes().stream().filter(gsi -> indexName.equals(gsi.getIndexName()))
                .findAny().orElseThrow().getIndexStatus().equals(IndexStatus.ACTIVE.toString()))
            //noinspection BusyWait
            Thread.sleep(5000);
    }
}