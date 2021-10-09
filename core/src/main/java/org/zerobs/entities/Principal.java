package org.zerobs.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Principal {
    private String sub;
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    private String iss;
    @JsonProperty("given_name")
    private String givenName;
    private String locale;
    private String picture;
    private String name;
    @JsonProperty("family_name")
    private String familyName;
    private String email;

    public void cloneAndSet(Principal principal) {
        sub = principal.sub;
        emailVerified = principal.emailVerified;
        iss = principal.iss;
        givenName = principal.givenName;
        locale = principal.locale;
        picture = principal.picture;
        name = principal.name;
        familyName = principal.familyName;
        email = principal.email;
    }
}