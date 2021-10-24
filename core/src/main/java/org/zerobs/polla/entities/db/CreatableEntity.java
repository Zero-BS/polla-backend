package org.zerobs.polla.entities.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;

@EqualsAndHashCode(callSuper = true)
@DynamoDBTable(tableName = TABLE_NAME)
@Data
public abstract class CreatableEntity extends Entity {
    @DynamoDBAttribute(attributeName = "updated_on")
    @JsonIgnore
    protected Long updatedOn;

    @DynamoDBAttribute(attributeName = "created_on")
    @JsonIgnore
    private Long createdOn;

    @DynamoDBAttribute(attributeName = "created_by")
    @JsonIgnore
    private String createdBy;
}