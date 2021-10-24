package org.zerobs.polla.services;

import org.zerobs.polla.entities.Principal;
import org.zerobs.polla.entities.db.User;

import java.util.Locale;

public interface UserManager {
    void add(User user, Principal principal, Locale locale);

    User get(Principal principal);

    void delete(Principal principal);
}