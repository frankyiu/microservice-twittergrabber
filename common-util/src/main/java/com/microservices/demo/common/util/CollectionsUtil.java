package com.microservices.demo.common.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionsUtil {
    private CollectionsUtil(){}

    private static class CollectionsUtilHolder {
        static final CollectionsUtil INSTANT = new CollectionsUtil();
    }

    public static CollectionsUtil getInstance() {
        return CollectionsUtilHolder.INSTANT;
    }

    public <T> List<T> getListFromIterable(Iterable<T> iterable){
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
