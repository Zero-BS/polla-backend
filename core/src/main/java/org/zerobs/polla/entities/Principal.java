package org.zerobs.polla.entities;

import com.amazonaws.serverless.proxy.model.ApiGatewayAuthorizerContext;
import lombok.Getter;

@Getter
public class Principal {
    private final String subject;
    private final Boolean emailVerified;
    private final String issuer;
    private final String authority;
    private final String givenName;
    private final String locale;
    private final String pictureUrl;
    private final String name;
    private final String familyName;
    private final String email;

    public Principal(ApiGatewayAuthorizerContext context) {
        subject = context.getPrincipalId();
        emailVerified = Boolean.valueOf(context.getContextValue("email_verified"));
        issuer = context.getContextValue("iss");
        authority = context.getContextValue("authority");
        givenName = context.getContextValue("given_name");
        locale = context.getContextValue("locale");
        pictureUrl = context.getContextValue("picture");
        name = context.getContextValue("name");
        familyName = context.getContextValue("family_name");
        email = context.getContextValue("email");
    }
}