package org.zerobs.polla.utilities;


import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Utils {
    private Utils() {
    }

    public static <T> List<T> cleanList(List<T> list) {
        return emptyIfNull(list).stream().filter(item -> {
            if (item instanceof String) return StringUtils.isNotBlank((String) item);
            else return item != null;
        }).map(item -> {
            if (item instanceof String) return (T) ((String) item).trim();
            else return item;
        }).collect(toList());
    }

    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }
}