package org.zerobs.polla.test.utils;

import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import io.micronaut.http.HttpMethod;

public class Util {
    public static AwsProxyRequest getRequest(String path, HttpMethod httpMethod) {
        return getRequest(path, httpMethod, null);
    }

    public static AwsProxyRequest getRequest(String path, HttpMethod httpMethod, String body) {
        String principalId = "12345678";
        String givenName = "Green";
        String familyName = "Demogoblin";

        return new AwsProxyRequestBuilder(path, httpMethod.toString())
                .authorizerPrincipal(principalId)
                .authorizerContextValue("iss", "https://accounts.google.com")
                .authorizerContextValue("sub", principalId)
                .authorizerContextValue("email", "abc@xyz.com")
                .authorizerContextValue("email_verified", "true")
                .authorizerContextValue("given_name", givenName)
                .authorizerContextValue("family_name", familyName)
                .authorizerContextValue("name", givenName + " " + familyName)
                .authorizerContextValue("locale", "en")
                .authorizerContextValue("picture", "https://lh3.googleusercontent.com/a-/sdfghjk3456789")
                .authorizerContextValue("authority", "GOOGLE")
                .body(body)
                .build();
    }
}
