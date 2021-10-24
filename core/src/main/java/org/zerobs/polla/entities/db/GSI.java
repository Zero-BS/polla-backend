package org.zerobs.polla.entities.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GSI {
    private final String indexName;
    private final String pkAttributeName;
    private final String skAttributeName;

    public GSI(String indexName, String pkAttributeName) {
        this(indexName, pkAttributeName, null);
    }
}