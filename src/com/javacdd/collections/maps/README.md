## 1. Why HashMap Exists (The Core Idea)

HashMap exists to solve one problem efficiently:

> **Fast access to data using a key.**

Instead of searching sequentially (like a list), HashMap uses **hashing** to directly compute *where* a value should live in memory.

This is why HashMap is a backbone structure in backend systems.

---

## 2. What HashMap Is (and Is Not)

### What it IS
- An in-memory **key–value** data structure
- Optimized for **fast lookup**
- A supporting layer in backend systems

### What it is NOT
- A database
- A persistent store
- A distributed cache
- A thread-safe structure

Think of HashMap as **speed**, not **truth**.

---

## 3. Key Characteristics

- Stores data as `key → value`
- Average time complexity: **O(1)** for `put()` and `get()`
- Allows **one null key**, multiple null values
- Order is **not guaranteed**
- **Not thread-safe**

---

## 4. High-Level Internal Structure

At runtime, HashMap internally maintains:

- An **array of buckets**
- Each bucket contains:
    - A linked list (default)
    - A **Red-Black Tree** (Java 8+) when collisions increase

---
## 5. Internal Structure Diagram (Mental Model)
HashMap (table size = n)
bucket[0] ──► null

bucket[1] ──► (A, 10) ──► (B, 20)

bucket[2] ──► (C, 30)

bucket[3] ──► TreeNode (Red-Black Tree)

├── (D, 40)

├── (E, 50)

└── (F, 60)


bucket[n] ──► null

Key points:
- Bucket index is derived from the key’s hash
- Multiple keys can land in the same bucket (collision)
- Too many collisions → tree instead of list
---
## 6. How HashMap Calculates Bucket Index

When a key is used:

1. `hashCode()` is called on the key
2. Hash is spread using bit manipulation
3. Final index is computed as:   
`index = (capacity - 1) & hash`
Why this matters:
- Faster than modulo `%`
- Works only when capacity is a power of 2
- Improves uniform distribution
---

## 7. Internal Working of `put(key, value)`

Step-by-step flow:

1. If internal array is null → initialize it
2. Compute hash from key
3. Calculate bucket index
4. Check bucket:
    - Empty → insert node
    - Not empty → collision
5. On collision:
    - Compare keys using `equals()`
    - If key matches → replace value
    - Else → add new node
6. If bucket size crosses threshold:
    - Convert linked list → Red-Black Tree
7. If overall size crosses load factor:
    - Resize HashMap

---
## 8. Internal Working of `get(key)`

1. Compute hash of key
2. Calculate bucket index
3. Traverse bucket:
    - Match hash
    - Match key using `equals()`
4. Return value if found
5. Return `null` if not found

Important:
> `hashCode()` finds the bucket,  
> `equals()` finds the exact key.

---

## 9. The equals() & hashCode() Contract (CRITICAL)

Rules:
- If two objects are equal → their hashCode **must** be same
- Same hashCode ≠ same object

Why both exist:
- `hashCode()` → performance
- `equals()` → correctness

Breaking this contract causes:
- Duplicate logical keys
- Failed lookups
- Silent data corruption

---

## 10. Collision Handling Strategy

### What is a collision?
When multiple keys map to the same bucket index.

### How Java handles it:
- Java 7 and earlier → Linked List
- Java 8+:
    - Linked List initially
    - Converts to **Red-Black Tree** when:
        - Bucket size > 8
        - Capacity ≥ 64

### Benefit:
- Worst-case time improves from **O(n)** to **O(log n)**

---

## 11. Load Factor & Resizing

- Default initial capacity: **16**
- Default load factor: **0.75**

Meaning:
- Resize when 75% full
- New capacity = old capacity × 2
- All entries are rehashed

Resizing is expensive — frequent resizing hurts performance.

---

## 12. Handling of null

- One `null` key allowed
- Multiple `null` values allowed

Why:
- `null` key is treated as hash = 0
- Always stored in bucket index 0

---

## 13. Time Complexity Summary

| Operation | Average | Worst Case |
|---------|--------|------------|
| put()   | O(1)   | O(log n)   |
| get()   | O(1)   | O(log n)   |
| remove()| O(1)   | O(log n)   |

Worst case improved due to treeification in Java 8+.

---

## 14. HashMap in Multi-Threaded Systems

HashMap is **not thread-safe**.

Problems:
- Data inconsistency
- Lost updates
- Infinite loops during resize (classic bug)

Correct alternatives:
- `ConcurrentHashMap`
- Distributed caches (Redis, Hazelcast)

---
## Real-World HashMap Applications
**URL Shortener Service ([URLShortenerService.java](https://github.com/shivengg87/java-core-deep-dive/blob/main/src/com/javacdd/collections/maps/URLShortenerService.java))**
What it does:

- Converts long URLs into short, shareable links

- Bidirectional mapping (short ↔ long URL)

- Collision handling and duplicate prevention

- Usage statistics tracking
```
URLShortenerService service = new URLShortenerService();
String shortUrl = service.shortenURL("https://example.com/very/long/url");
// Returns: "https://short.ly/aB3xY9z"

String original = service.expandURL(shortUrl);
// Returns: "https://example.com/very/long/url"
```
## HashMap Output-Based Puzzle
```
# Basic put & overwrite
Map<Integer, String> map = new HashMap<>();
map.put(1, "A");
map.put(1, "B");
map.put(2, "C");

System.out.println(map);
```
✅ Output
```
{1=B, 2=C}
```
Why
- Keys must be unique
- put(1, "B") overwrites value for key 1
- HashMap does not allow duplicate keys
---
```
# Order of Insertion
Map<Integer, String> map = new HashMap<>();
map.put(3, "C");
map.put(1, "A");
map.put(2, "B");

System.out.println(map);   
```
✅ Output
```
Unpredictable order
```
Why
- HashMap does NOT maintain insertion order
- Order depends on hashing & bucket placement
---
```
# Null key behavior
Map<String, Integer> map = new HashMap<>();
map.put(null, 10);
map.put(null, 20);
map.put("A", 30);

System.out.println(map);
```
✅ Output
```
{null=20, A=30}
```
Why
- HashMap allows only one null key
- Second put(null, 20) overwrites first value
---
```
# equals() without hashCode() 
class Key {
    int id;
    Key(int id) { this.id = id; }

    public boolean equals(Object o) {
        return ((Key)o).id == this.id;
    }
}

Map<Key, String> map = new HashMap<>();
map.put(new Key(1), "A");
map.put(new Key(1), "B");

System.out.println(map.size());
```
✅ Output
```
2
```
Why

Even though equals() is overridden, hashCode() is not.
As a result, logically equal objects can produce different hash values and end up in different buckets.
Since HashMap only calls equals() after keys land in the same bucket, these entries are treated as different keys.
---
```
# equals + hashCode both overridden
class Key {
    int id;
    Key(int id) { this.id = id; }

    public boolean equals(Object o) {
        return ((Key)o).id == this.id;
    }

    public int hashCode() {
        return id;
    }
}

Map<Key, String> map = new HashMap<>();
map.put(new Key(1), "A");
map.put(new Key(1), "B");

System.out.println(map.size());
```
✅ Output
```
1
```
Why:

**HashMap first uses hashCode() to decide which bucket a key should go into.**
If another key lands in the same bucket, HashMap then calls equals().
When equals() returns true, HashMap treats both keys as the same logical key and overwrites the existing value instead of adding a new entry.
---
```
# Mutable Key 
class Key {
    int id;
    Key(int id) { this.id = id; }

    public int hashCode() { return id; }
    public boolean equals(Object o) {
        return ((Key)o).id == this.id;
    }
}

Key k = new Key(1);
Map<Key, String> map = new HashMap<>();
map.put(k, "A");

k.id = 2;

System.out.println(map.get(k));
```
✅ Output
```
null
```
Why:

**HashMap uses the key’s hashCode() at the time of insertion to determine the bucket where the entry is stored.**
If the key’s internal state changes later, its hashCode() may change as well, breaking the original bucket mapping.
As a result, the entry still exists inside the map but becomes unreachable, because lookups search a different bucket.
---
```
# containsKey vs containsValue
Map<Integer, String> map = new HashMap<>();
map.put(1, "A");
map.put(2, "B");

System.out.println(map.containsKey(1));
System.out.println(map.containsValue("B"));
System.out.println(map.containsKey("A"));
```
✅ Output
```
true
true
false
```
```
Map<Integer, Integer> map = new HashMap<>();

for (int i = 0; i < 3; i++) {
    map.put(i, i + 1);
}

System.out.println(map.get(2));
```
✅ Output
```
3
```
Why :

In each iteration, the loop index i is used as the key, and i + 1 is used as the value.
When i becomes 2, the entry (2 → 3) is inserted into the map.
Therefore, map.get(2) returns 3.

```
# Iteration modification
Map<Integer, String> map = new HashMap<>();
map.put(1, "A");
map.put(2, "B");

for (Integer key : map.keySet()) {
    map.put(3, "C");
}
```
✅ Output
```
ConcurrentModificationException
```
Why :

**HashMap does not allow you to change its size while you are looping over it.**
ConcurrentModificationException occurs because HashMap iterators are fail-fast and do not allow structural changes while iterating. When a loop starts, the iterator expects the map’s structure (number of entries) to remain unchanged. If a key is added or removed during iteration using operations like put() or remove(), HashMap immediately detects this change and throws the exception to prevent inconsistent traversal
