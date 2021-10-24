package org.zerobs.polla.repositories;

import org.zerobs.polla.entities.db.User;

import jakarta.inject.Singleton;

@Singleton
public class DefaultUserRepository extends DefaultEntityRepository<User> implements UserRepository {
    @Override
    public boolean usernameExists(String username) {
        return getByIndex(User.GSI_USERNAME, username) != null;
    }
}