package org.zerobs.polla.controllers;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import org.zerobs.polla.entities.Principal;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.exception.CustomRuntimeException;
import org.zerobs.polla.services.UserManager;

import java.util.Locale;

import static org.zerobs.polla.exception.RuntimeExceptionType.INVALID_USER;

@Controller("/v1/users")
public class UserController {
    @Inject
    private UserManager userManager;

    @Post
    @Status(HttpStatus.CREATED)
    public void add(AwsProxyRequest awsProxyRequest, Locale locale, @Body @Nullable User user) {
        userManager.add(user, new Principal(awsProxyRequest.getRequestContext().getAuthorizer()), locale);
    }

    @Get
    public User get(AwsProxyRequest awsProxyRequest) {
        var user = userManager.get(new Principal(awsProxyRequest.getRequestContext().getAuthorizer()));
        if (user == null) throw new CustomRuntimeException(INVALID_USER);
        return user;
    }

    @Delete
    public void delete(AwsProxyRequest awsProxyRequest) {
        userManager.delete(new Principal(awsProxyRequest.getRequestContext().getAuthorizer()));
    }
}