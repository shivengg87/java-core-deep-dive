package com.javacdd.collections.maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates safe structural modification of ConcurrentHashMap
 * during iteration.
 *
 * Key idea:
 * ConcurrentHashMap allows concurrent reads and writes without
 * throwing ConcurrentModificationException.
 */
public class ConcurrentHashMapDemo {
    public static void main(String[] args) {
        // Thread-safe, high-concurrency Map implementation
        // Internally uses fine-grained locking and CAS operations
        Map<Long, String> cache = new ConcurrentHashMap<>();

        // Initial entries
        cache.put(1L, "ACTIVE");
        cache.put(2L, "INACTIVE");

        /**
         * forEach() in ConcurrentHashMap:
         * - Iteration is weakly consistent
         * - Reflects some, but not necessarily all, modifications
         * - Does NOT throw ConcurrentModificationException
         */
        cache.forEach((k, v) -> {

            // Structural modification during iteration is allowed
            // In HashMap, this would throw ConcurrentModificationException
            cache.put(3L, "NEW"); // safe
        });
        // Map remains in a consistent state
        System.out.println(cache);
    }
}
