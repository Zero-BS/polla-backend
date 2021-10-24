package org.zerobs.polla.repositories;

import org.zerobs.polla.entities.db.User;

public interface UserRepository extends EntityRepository<User> {
    boolean usernameExists(String username);
}