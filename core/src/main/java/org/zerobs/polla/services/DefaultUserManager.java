package org.zerobs.polla.services;

import com.github.javafaker.Faker;
import io.micronaut.core.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.zerobs.polla.entities.Principal;
import org.zerobs.polla.entities.db.User;
import org.zerobs.polla.exception.CustomRuntimeException;
import org.zerobs.polla.repositories.UserRepository;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Year;
import java.time.ZoneId;
import java.util.Locale;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonMap;
import static org.zerobs.polla.exception.RuntimeExceptionType.*;

@Singleton
public class DefaultUserManager implements UserManager {
    @Inject
    private UserRepository userRepository;

    @Override
    public void add(User user, Principal principal, Locale locale) {
        if (user == null)
            throw new CustomRuntimeException(EMPTY_USER);
        if (get(principal) != null)
            throw new CustomRuntimeException(EXISTING_USER);
        if (StringUtils.isBlank(user.getUsername()))
            throw new CustomRuntimeException(EMPTY_USERNAME, singletonMap("username.suggestion", getUsernameSuggestion(locale)));

        user.setUsername(user.getUsername().trim());
        if (userRepository.usernameExists(user.getUsername()))
            throw new CustomRuntimeException(EXISTING_USERNAME, singletonMap("username.suggestion", getUsernameSuggestion(locale)));

        if (user.getYearOfBirth() == null)
            throw new CustomRuntimeException(EMPTY_YEAR_OF_BIRTH);

        int currentYear = Year.now(ZoneId.of("UTC")).getValue();
        int age = currentYear - user.getYearOfBirth();
        if (age < 3)
            throw new CustomRuntimeException(TOO_YOUNG_USER);
        if (age > 130)
            throw new CustomRuntimeException(TOO_OLD_USER);

        if (user.getGender() == null)
            throw new CustomRuntimeException(EMPTY_GENDER);


        user.setId(principal);
        user.setLocale(principal.getLocale());
        user.setEmail(principal.getEmail());
        user.setEmailVerified(principal.getEmailVerified());
        user.setCreatedOn(currentTimeMillis());
        user.setUpdatedOn(user.getCreatedOn());
        userRepository.save(user);
    }

    @Override
    @Nullable
    public User get(Principal principal) {
        var user = new User(principal);
        user = userRepository.getByPk(user.getPk());
        if (user == null)
            return null;
        if (!user.getEmail().equals(principal.getEmail())) {
            user.setEmail(principal.getEmail());
            user.setUpdatedOn(currentTimeMillis());
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public void delete(Principal principal) {
        userRepository.delete(new User(principal));
    }

    private String getUsernameSuggestion(Locale locale) {
        var faker = new Faker(locale);
        return faker.superhero().name().replace(" ", "") + "_" + faker.random().hex(4).toLowerCase(locale);
    }
}