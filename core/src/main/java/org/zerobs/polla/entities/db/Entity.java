package org.zerobs.polla.entities.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Locale;

@Data
@DynamoDbBean
public abstract class Entity {
    protected static final String SEPARATOR = "#";
    private static final String ENTITY_IDENTIFIER = "ENTITY";

    @JsonIgnore
    private final String entityClassIdentifier = getClass().getSimpleName().toUpperCase(Locale.ROOT);

    protected String id;

    @JsonIgnore
    protected String pk;

    @JsonIgnore
    protected String sk = ENTITY_IDENTIFIER;

    @DynamoDbIgnore
    @JsonIgnore
    protected String getPkInitials() {
        return entityClassIdentifier + SEPARATOR;
    }

    @DynamoDbPartitionKey
    public String getPk() {
        return getPkInitials() + id;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    @DynamoDbIgnore
    public String getEntityIdentifier() {
        return entityClassIdentifier;
    }

    public void setPk(String pk) {
        this.pk = pk;
        id = pk.substring(getPkInitials().length());
    }
}