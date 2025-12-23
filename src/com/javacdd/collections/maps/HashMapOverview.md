# HashMap – Overview

HashMap is a key–value based data structure provided by the Java Collections Framework.
It is designed for fast in-memory access using hashing.

Key characteristics:
- Stores data as key–value pairs
- Average time complexity: O(1) for get() and put()
- Allows one null key and multiple null values
- Does not maintain insertion or sorted order
- Not thread-safe

HashMap is widely used in backend systems as:
- In-memory lookup table
- Cache layer (small to medium scale)
- Aggregation and grouping structure
- Temporary processing store

Important:
HashMap is NOT a replacement for a database.
It is an access-optimization layer.
