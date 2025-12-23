# java-core-deep-dive
A deep, practical exploration of Java internals — focused on **JVM behavior, memory management, concurrency, and performance pitfalls** that matter in real-world systems.

### JVM Internals
- Heap vs Stack vs Metaspace
- Object lifecycle
- JVM memory layout
- Garbage Collection fundamentals
- GC algorithms and trade-offs

### Class Loading
- ClassLoader hierarchy
- Custom class loaders
- When and why `ClassNotFoundException` happens
- Class loading vs initialization

### Java Language Deep Dive
- Immutability (and how it breaks)
- `equals()` / `hashCode()` contracts
- String pool behavior
- Autoboxing and hidden allocations

### Concurrency & Multithreading
- Thread lifecycle
- `synchronized` vs `Lock`
- Deadlocks and livelocks
- Executors and thread pool sizing
- `CompletableFuture` and Fork/Join

### Java Memory Model (JMM)
- Visibility vs atomicity
- `volatile` semantics
- Happens-before rules
- Why some multithreaded bugs feel “random”

### Performance & Optimization
- Boxing / unboxing costs
- Escape analysis
- JIT compilation basics
- When micro-optimizations hurt more than help
