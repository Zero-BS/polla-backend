package org.zerobs.polla.utilities;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Utils {
    private Utils() {
    }

    public static <T> List<T> cleanList(List<T> list) {
        return ListUtils.emptyIfNull(list).stream().filter(item -> {
            if (item instanceof String) return StringUtils.isNotBlank((String) item);
            else return item != null;
        }).collect(toList());
    }
}