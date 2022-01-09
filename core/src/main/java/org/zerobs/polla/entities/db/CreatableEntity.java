package org.zerobs.polla.entities.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@EqualsAndHashCode(callSuper = true)
@DynamoDbBean
@Data
public abstract class CreatableEntity extends Entity {
    @JsonIgnore
    protected Long updatedOn;

    @JsonIgnore
    private Long createdOn;

    @JsonIgnore
    private String createdBy;

    @DynamoDbAttribute("updated_on")
    protected Long getUpdatedOn() {
        return updatedOn;
    }

    @DynamoDbAttribute("created_on")
    public Long getCreatedOn() {
        return createdOn;
    }

    @DynamoDbAttribute("created_by")
    public String getCreatedBy() {
        return createdBy;
    }
}