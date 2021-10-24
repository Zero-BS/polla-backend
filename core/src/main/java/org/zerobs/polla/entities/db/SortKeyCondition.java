package org.zerobs.polla.entities.db;

import lombok.Getter;

@Getter
public enum SortKeyCondition {
    EQUAL_TO(" = "),
    BEGINS_WITH("begins_with");

    private final String conditionString;

    SortKeyCondition(String conditionString) {
        this.conditionString = conditionString;
    }
}