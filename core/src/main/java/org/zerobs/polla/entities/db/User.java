package org.zerobs.polla.entities.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zerobs.polla.entities.Gender;
import org.zerobs.polla.entities.Principal;
import org.zerobs.polla.utilities.DbGenderConverter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@DynamoDbBean
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class User extends CreatableEntity {
    public static final String PK_USERNAME_GSI = "username";
    public static final String GSI_NAME_USERNAME = "GSI-username";
    public static final GSI GSI_USERNAME = new GSI(GSI_NAME_USERNAME, PK_USERNAME_GSI);
    private String username;
    private Integer yearOfBirth;
    private Gender gender;
    private String locale;
    private String email;
    private Boolean emailVerified;
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

    @DynamoDbSecondaryPartitionKey(indexNames = {GSI_NAME_USERNAME})
    public String getUsername() {
        return username;
    }

    @DynamoDbAttribute("year_of_birth")
    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    @DynamoDbConvertedBy(DbGenderConverter.class)
    public Gender getGender() {
        return gender;
    }

    @DynamoDbAttribute("email_verified")
    public Boolean isEmailVerified() {
        return emailVerified;
    }

    @DynamoDbIgnore
    public String getCreatedBy() {
        return null;
    }
}