# HashMap – Internal Working

Understanding HashMap internals is critical for writing bug-free and efficient backend code.

---

## Internal Structure
- HashMap internally uses an array of buckets
- Each bucket stores nodes (key, value, hash)
- Collision handling:
    - Linked List (before Java 8)
    - Red-Black Tree (Java 8+) when threshold exceeded

---

## How put() works
1. hashCode() is called on the key
2. Hash value is transformed to bucket index
3. Bucket is checked:
    - Empty → insert node
    - Collision → compare keys using equals()
4. If key exists → value replaced
5. If collision chain grows large → treeification occurs

---

## How get() works
1. hashCode() → bucket index
2. Traverse bucket
3. equals() used to find exact key
4. Value returned

---

## Why equals() and hashCode() matter
Rules:
- If two objects are equal → same hashCode
- Same hashCode does NOT mean equal

Breaking this contract causes:
- Duplicate keys
- Data loss
- Lookup failures

---

## Java 8 Improvement
- Linked list converts to Red-Black Tree after threshold
- Worst-case lookup improves to O(log n)

---

## Load Factor
Default load factor = 0.75

Meaning:
- Resize happens when 75% full
- Balances memory and performance

---

## Important Notes
- HashMap allows null key (hash = 0)
- Iteration order is unpredictable
- Resizing is expensive
