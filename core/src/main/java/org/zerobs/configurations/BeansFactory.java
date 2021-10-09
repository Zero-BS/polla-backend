package org.zerobs.configurations;

import io.micronaut.context.annotation.Factory;
import org.zerobs.entities.Principal;

import javax.inject.Singleton;

@Factory
public class BeansFactory {
    @Singleton
    public Principal principal() {
        return new Principal();
    }
}
