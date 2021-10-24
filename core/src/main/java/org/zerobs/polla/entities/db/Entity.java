package org.zerobs.polla.entities.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Locale;

import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;

@Data
@DynamoDBTable(tableName = TABLE_NAME)
public abstract class Entity {
    protected static final String SEPARATOR = "#";
    private static final String ENTITY_IDENTIFIER = "ENTITY";

    @JsonIgnore
    @DynamoDBIgnore
    private final String entityClassIdentifier = getClass().getSimpleName().toUpperCase(Locale.ROOT);

    @DynamoDBIgnore
    protected String id;

    @JsonIgnore
    protected String pk;

    @DynamoDBRangeKey
    @JsonIgnore
    protected String sk = ENTITY_IDENTIFIER;

    @DynamoDBIgnore
    @JsonIgnore
    protected String getPkInitials() {
        return entityClassIdentifier + SEPARATOR;
    }

    @DynamoDBHashKey
    public String getPk() {
        return getPkInitials() + id;
    }

    public void setPk(String pk) {
        this.pk = pk;
        id = pk.substring(getPkInitials().length());
    }
}