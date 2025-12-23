package com.javacdd.collections.maps;

import java.util.HashMap;
import java.util.Map;
public class HashMapDemo {
    public static void main(String[] args) {
        Map<Long, String> employeeMap = new HashMap<>();
        employeeMap.put(101L, "Shivram");
        employeeMap.put(102L, "Pavnee");
        String employee = employeeMap.get(101L);
        System.out.println("Employee Name is "+employee);
       }
}
