package org.zerobs.polla.entities.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import javax.validation.constraints.Null;
import java.util.Locale;

@EqualsAndHashCode(callSuper = true)
@DynamoDbBean
@Data
@NoArgsConstructor
public class Tag extends CreatableEntity {
    public static final String GSI_NAME_TAG_NAME = "GSI-tag-name";
    public static final String PK_TAG_NAME_GSI = "tag_name_gsi_pk";
    public static final String SK_TAG_NAME_GSI = "lowered_name_name";
    public static final GSI GSI_TAG_NAME = new GSI(GSI_NAME_TAG_NAME, PK_TAG_NAME_GSI, SK_TAG_NAME_GSI);
    @JsonIgnore
    public static final String TAG_GSI_PK_VALUE = "TAG";
    @JsonIgnore
    @Null
    protected Long updatedOn;
    private String name;
    private Integer followers;
    @JsonIgnore
    private String loweredNameName;
    @JsonIgnore
    private boolean newTag;
    @Setter(AccessLevel.NONE)
    private String tagGsiPk;

    public Tag(String name) {
        this.name = name;
    }

    @DynamoDbAttribute(SK_TAG_NAME_GSI)
    @DynamoDbSecondarySortKey(indexNames = {GSI_NAME_TAG_NAME})
    public String getLoweredNameName() {
        if (name == null)
            return null;
        return name.toLowerCase(Locale.ROOT) + SEPARATOR + name;
    }

    @DynamoDbIgnore
    protected Long getUpdatedOn() {
        return null;
    }

    @DynamoDbIgnore
    public boolean isNewTag() {
        return newTag;
    }

    @DynamoDbAttribute(PK_TAG_NAME_GSI)
    @DynamoDbSecondaryPartitionKey(indexNames = {GSI_NAME_TAG_NAME})
    public String getTagGsiPk() {
        return TAG_GSI_PK_VALUE;
    }
}