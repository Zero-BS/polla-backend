package org.zerobs.polla.entities.db;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zerobs.polla.entities.Gender;
import org.zerobs.polla.entities.Principal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;

@DynamoDBTable(tableName = TABLE_NAME)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class User extends CreatableEntity {
    public static final String PK_USERNAME_GSI = "username";
    public static final String GSI_NAME_USERNAME = "GSI-username";
    public static final GSI GSI_USERNAME = new GSI(GSI_NAME_USERNAME, PK_USERNAME_GSI);
    @DynamoDBIndexHashKey(globalSecondaryIndexName = GSI_NAME_USERNAME)
    private String username;
    @DynamoDBAttribute(attributeName = "year_of_birth")
    private Integer yearOfBirth;
    @DynamoDBTypeConvertedEnum
    private Gender gender;
    private String locale;
    private String email;
    @DynamoDBAttribute(attributeName = "email_verified")
    private Boolean emailVerified;
    @DynamoDBIgnore
    @Null
    private String createdBy;

    public User(Principal principal) {
        setId(principal);
    }

    @JsonCreator
    public User(@JsonProperty("id") String id, @JsonProperty("username") String username,
                @JsonProperty("year_of_birth") Integer yearOfBirth, @JsonProperty("gender") Gender gender,
                @JsonProperty("locale") String locale, @JsonProperty("email") String email,
                @JsonProperty("email_verified") boolean emailVerified) {
        this.id = id;
        this.username = username;
        this.yearOfBirth = yearOfBirth;
        this.gender = gender;
        this.locale = locale;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    public void setId(@NotNull Principal principal) {
        id = principal.getAuthority() + SEPARATOR + principal.getSubject();
    }
}