package org.zerobs.polla.repositories;

import jakarta.inject.Singleton;
import org.zerobs.polla.entities.db.User;

@Singleton
public class DefaultUserRepository extends DefaultEntityRepository<User> implements UserRepository {
    @Override
    public boolean usernameExists(String username) {
        return getByIndex(User.GSI_USERNAME, username) != null;
    }
}