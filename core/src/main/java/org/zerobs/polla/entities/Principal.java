package org.zerobs.polla.entities;

import com.amazonaws.serverless.proxy.model.ApiGatewayAuthorizerContext;
import lombok.Getter;

@Getter
public class Principal {
    private final String subject;
    private final Boolean emailVerified;
    private final String authority;
    private final String locale;
    private final String email;

    public Principal(ApiGatewayAuthorizerContext context) {
        subject = context.getPrincipalId();
        emailVerified = Boolean.valueOf(context.getContextValue("email_verified"));
        authority = context.getContextValue("authority");
        locale = context.getContextValue("locale");
        email = context.getContextValue("email");
    }
}