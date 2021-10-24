package org.zerobs.polla.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.zerobs.polla.entities.db.Entity;
import org.zerobs.polla.exception.CustomException;

import java.util.List;

public interface EntityRepository<T extends Entity> {
    void save(T object);

    void batchSave(Iterable<T> objects) throws CustomException;

    T getByPk(String pk);

    void delete(T object);

    List<DynamoDBMapper.FailedBatch> batchDelete(Iterable<T> objects);
}