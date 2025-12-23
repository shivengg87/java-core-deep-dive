package com.javacdd.collections.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * Custom key class used in HashMap.
 *
 * Important:
 * HashMap relies on BOTH equals() and hashCode() to identify
 * whether two keys are logically the same.
 */
class Employee {
    // Immutable fields are critical for HashMap keys
    // If these values change after insertion, the entry becomes unreachable
    private final long id;
    private final String name;

    Employee(Long id, String name)
    {
        this.id = id;
        this.name= name;
    }
    /**
     * equals() defines logical equality.
     *
     * Two Employee objects are considered equal if their 'id' is equal,
     * regardless of other fields like name.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        Employee e = (Employee) obj;
        return Objects.equals(id, e.id);
    }
    /**
     * hashCode() must be consistent with equals().
     *
     * Rule:
     * If two objects are equal according to equals(),
     * they MUST return the same hashCode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
/**
 * Demonstrates correct HashMap behavior when using
 * a custom object as a key.
 */
public class HashMapWithCustomKey {
    public static void main(String[] args) {
        // HashMap uses equals() and hashCode() to detect duplicate keys
        Map<Employee,String> employeeMap = new HashMap<>();
        // Two different objects in memory
        Employee e1 = new Employee(1L,"Shivram");
        Employee e2 = new Employee(1L, "Shivram");
        // First put() inserts the key-value pair
        employeeMap.put(e1,"Developer");
        // Second put() replaces the value because:
        // - hashCode() is same
        // - equals() returns true
        employeeMap.put(e2,"Architect");
        // Size is 1 because HashMap treats e1 and e2 as the same key
        System.out.println("Size="+employeeMap.size());
    }
}
