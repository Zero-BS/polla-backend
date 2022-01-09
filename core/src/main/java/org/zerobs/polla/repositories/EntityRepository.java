package org.zerobs.polla.repositories;

import org.zerobs.polla.entities.db.Entity;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;

public interface EntityRepository<T extends Entity> {
    void save(T object);

    void batchSave(Iterable<T> objects);

    T get(T item);

    T get(String pk, String sk);

    void delete(T object);

    List<Key> batchDelete(Iterable<T> objects);
}