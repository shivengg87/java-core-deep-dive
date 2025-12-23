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
