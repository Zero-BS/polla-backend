package org.zerobs.polla.repositories;

import com.google.common.reflect.TypeToken;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.zerobs.polla.entities.db.Entity;
import org.zerobs.polla.entities.db.GSI;
import org.zerobs.polla.entities.db.SortKeyCondition;
import org.zerobs.polla.exception.CustomRuntimeException;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.zerobs.polla.constants.ApplicationConstants.TABLE_NAME;
import static org.zerobs.polla.exception.RuntimeExceptionType.FAILED_BATCH_SAVE;

@Singleton
@Slf4j
public class DefaultEntityRepository<T extends Entity> implements EntityRepository<T> {
    private final TypeToken<T> typeToken = new TypeToken<>(getClass()) {
    };
    private final Class<T> genericType = (Class<T>) typeToken.getRawType();
    @Inject
    private DynamoDbClient dynamoDbClient;
    @Inject
    private DynamoDbEnhancedClient dbEnhancedClient;

    @Override
    public void save(T object) {
        getTable().putItem(object);
    }

    @Override
    public void batchSave(Iterable<T> objects) {
        WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(genericType).mappedTableResource(getTable());
        objects.forEach(writeBatchBuilder::addPutItem);
        BatchWriteItemEnhancedRequest batchWriteRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatchBuilder.build()).build();
        BatchWriteResult batchWriteResult = dbEnhancedClient.batchWriteItem(batchWriteRequest);
        List<T> failedItems = batchWriteResult.unprocessedPutItemsForTable(getTable());
        if (!failedItems.isEmpty()) {
            //rollback
            try {
                batchDelete(objects);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            throw new CustomRuntimeException(FAILED_BATCH_SAVE, singletonMap("failed.items", failedItems));
        }
    }

    @Override
    public T get(T item) {
        return get(item.getPk(), item.getSk());
    }

    @Override
    public T get(String pk, String sk) {
        return getTable().getItem(Key.builder().partitionValue(pk).sortValue(sk).build());
    }

    @Override
    public void delete(T object) {
        getTable().deleteItem(Key.builder().partitionValue(object.getPk()).sortValue(object.getSk()).build());
    }

    @Override
    public List<Key> batchDelete(Iterable<T> objects) {
        WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(genericType).mappedTableResource(getTable());
        objects.forEach(writeBatchBuilder::addDeleteItem);
        BatchWriteItemEnhancedRequest batchWriteRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatchBuilder.build()).build();
        return dbEnhancedClient.batchWriteItem(batchWriteRequest).unprocessedDeleteItemsForTable(getTable());
    }

    protected T getByIndex(GSI gsi, String pkValue) {
        return getByIndex(gsi, pkValue, null, null);
    }

    protected T getByIndex(GSI gsi, String pkValue, String skValue) {
        return getByIndex(gsi, pkValue, skValue, SortKeyCondition.EQUAL_TO);
    }

    protected T getByIndex(GSI gsi, String pkValue, String skValue, SortKeyCondition sortKeyCondition) {
        return getByIndex(gsi, pkValue, skValue, sortKeyCondition, 1).stream().findFirst().orElse(null);
    }

    protected List<T> getByIndex(GSI gsi, String pkValue, String skValue,
                                 SortKeyCondition sortKeyCondition, int limit) {
        QueryEnhancedRequest.Builder queryRequestBuilder = QueryEnhancedRequest.builder()
                .consistentRead(false)
                .limit(limit)
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(pkValue).build()));

        if (gsi.getSkAttributeName() != null) {
            switch (sortKeyCondition) {
                case EQUAL_TO:
                    queryRequestBuilder.queryConditional(QueryConditional.keyEqualTo(Key.builder().sortValue(skValue).build()));
                    break;
                case BEGINS_WITH:
                    queryRequestBuilder.queryConditional(QueryConditional.sortBeginsWith(Key.builder().sortValue(skValue).build()));
                    break;
            }
        }

        SdkIterable<Page<T>> queryResult = getTable().index(gsi.getIndexName()).query(queryRequestBuilder.build());
        return queryResult.stream().flatMap(page -> page.items().stream()).collect(toList());
    }

    private DynamoDbTable<T> getTable() {
        return dbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(genericType));
    }
}