package com.javacdd.collections.maps;

/**
 * A simplified implementation of HashMap to demonstrate core concepts:
 * - Hashing and bucket selection
 * - Collision handling via separate chaining
 * - Dynamic resizing and rehashing
 * - Generic type support
 *
 * This implementation uses separate chaining (linked list) for collision resolution.
 * Java's actual HashMap uses a hybrid approach: linked list that converts to
 * balanced tree when chain length exceeds threshold.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class CustomHashMap<K, V> {

    /**
     * Internal node class representing a key-value pair in the hash table.
     * Each node contains a reference to the next node, forming a linked list
     * for collision handling.
     */
    private static class Entry<K, V> {
        final K key;
        V value;
        final int hash;
        Entry<K, V> next;

        Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }

    // Default initial capacity - must be power of 2 for efficient modulo operation
    private static final int DEFAULT_CAPACITY = 16;

    // Load factor - map will resize when size exceeds capacity * loadFactor
    private static final float LOAD_FACTOR = 0.75f;

    // The table, resized as necessary. Length MUST always be a power of two
    private Entry<K, V>[] table;

    // Current number of key-value mappings
    private int size;

    // Threshold for resizing: capacity * loadFactor
    private int threshold;

    /**
     * Constructs an empty HashMap with default capacity (16)
     */
    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.table = new Entry[DEFAULT_CAPACITY];
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        this.size = 0;
    }

    /**
     * Constructs an empty HashMap with specified initial capacity
     */
    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
        }
        int capacity = tableSizeFor(initialCapacity);
        this.table = new Entry[capacity];
        this.threshold = (int) (capacity * LOAD_FACTOR);
        this.size = 0;
    }

    /**
     * Returns a power of two size for the given target capacity.
     * This ensures efficient bitwise operations for bucket selection.
     */
    private int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : n + 1;
    }

    /**
     * Computes hash code for the given key.
     * Applies supplemental hash function to defend against poor quality hash functions.
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        // XOR higher bits with lower bits to reduce collisions
        return h ^ (h >>> 16);
    }

    /**
     * Returns the index in the table for the given hash.
     * Uses bitwise AND instead of modulo for efficiency (works because table size is power of 2)
     */
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V put(K key, V value) {
        // Resize if necessary before adding new entry
        if (size >= threshold) {
            resize();
        }

        int hash = hash(key);
        int index = indexFor(hash, table.length);

        // Check if key already exists in the chain
        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || (key != null && key.equals(e.key)))) {
                // Key exists, update value
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        // Key doesn't exist, add new entry at the head of the chain
        addEntry(hash, key, value, index);
        return null;
    }

    /**
     * Adds a new entry to the specified bucket
     */
    private void addEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> e = table[bucketIndex];
        // Add new entry at the head (most recently added entries are accessed first)
        table[bucketIndex] = new Entry<>(key, value, hash, e);
        size++;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null
     */
    public V get(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        // Search through the chain at this index
        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || (key != null && key.equals(e.key)))) {
                return e.value;
            }
        }
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V remove(K key) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);

        Entry<K, V> prev = null;
        Entry<K, V> e = table[index];

        // Traverse the chain to find the entry
        while (e != null) {
            Entry<K, V> next = e.next;
            if (e.hash == hash && (e.key == key || (key != null && key.equals(e.key)))) {
                size--;
                if (prev == null) {
                    // Removing the head of the chain
                    table[index] = next;
                } else {
                    // Removing from middle or end of chain
                    prev.next = next;
                }
                return e.value;
            }
            prev = e;
            e = next;
        }
        return null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all of the mappings from this map.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        Entry<K, V>[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            tab[i] = null;
        }
        size = 0;
    }

    /**
     * Doubles the capacity of the hash table and rehashes all entries.
     * This is called when the size exceeds the threshold.
     *
     * Time Complexity: O(n) where n is the number of entries
     * This is why HashMap operations are "amortized O(1)" - occasional O(n) resize
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity * 2;

        // Create new table with double capacity
        Entry<K, V>[] newTable = new Entry[newCapacity];
        threshold = (int) (newCapacity * LOAD_FACTOR);

        // Rehash all existing entries into new table
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> e = oldTable[i];
            while (e != null) {
                Entry<K, V> next = e.next;
                int newIndex = indexFor(e.hash, newCapacity);
                e.next = newTable[newIndex];
                newTable[newIndex] = e;
                e = next;
            }
        }

        table = newTable;
    }

    /**
     * Returns statistics about the hash table for analysis.
     * Useful for understanding collision patterns and distribution.
     */
    public String getStatistics() {
        int usedBuckets = 0;
        int maxChainLength = 0;
        int totalChainLength = 0;

        for (Entry<K, V> e : table) {
            if (e != null) {
                usedBuckets++;
                int chainLength = 0;
                Entry<K, V> current = e;
                while (current != null) {
                    chainLength++;
                    current = current.next;
                }
                totalChainLength += chainLength;
                maxChainLength = Math.max(maxChainLength, chainLength);
            }
        }

        double avgChainLength = usedBuckets > 0 ? (double) totalChainLength / usedBuckets : 0;
        double loadFactor = (double) size / table.length;

        return String.format(
                "Capacity: %d, Size: %d, Load Factor: %.2f%n" +
                        "Used Buckets: %d/%d (%.2f%%)%n" +
                        "Avg Chain Length: %.2f, Max Chain Length: %d",
                table.length, size, loadFactor,
                usedBuckets, table.length, (double) usedBuckets / table.length * 100,
                avgChainLength, maxChainLength
        );
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (Entry<K, V> e : table) {
            while (e != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(e.key).append("=").append(e.value);
                first = false;
                e = e.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}