package org.zerobs.polla.configurations;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.locale.HttpLocaleResolver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Locale;
import java.util.Optional;

@Singleton
public class CustomLocaleResolver implements HttpLocaleResolver {
    @Inject
    private Locale defaultLocale;

    @Override
    @NonNull
    public Optional<Locale> resolve(HttpRequest<?> context) {
        return Optional.of(context.getLocale().orElse(defaultLocale));
    }

    @Override
    @NonNull
    public Locale resolveOrDefault(@NonNull HttpRequest<?> context) {
        return resolve(context).orElse(defaultLocale);
    }
}
