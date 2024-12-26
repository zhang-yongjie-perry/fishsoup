package com.fishsoup.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class CollectionUtils {

    public static <T> List<List<T>> partitionList(List<T> list, int size, int partitionNum) {
        List<List<T>> partitions = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < list.size(); i += size) {
            int toIndex = Math.min(index + size, list.size());
            partitions.add(list.subList(index, toIndex));
            index = toIndex;
        }
        return partitionNum == -1 ? partitions : partitions.subList(0, Math.min(partitionNum, list.size()));
    }

    public static <T> List<List<T>> partition(List<T> list, int size, int partitionNum) {
        List<List<T>> partitions = IntStream.range(0, (list.size() + size - 1) / size)
            .mapToObj(i -> list.stream().skip((long) i * size).limit(size).toList()).toList();
        return partitionNum == -1 ? partitions : partitions.subList(0, Math.min(partitionNum, list.size()));
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
