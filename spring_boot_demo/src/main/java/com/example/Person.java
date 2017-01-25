package com.example;

import lombok.Data;


@Data
public class Person {
    private String name;
    private int age;

    public Person() {
        super();
    }
    public Person(String name, int age) {
        super();
        this.name = name;
        this.age = age;
    }

}