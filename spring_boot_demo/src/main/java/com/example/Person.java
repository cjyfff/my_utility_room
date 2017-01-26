package com.example;

import lombok.Data;

import java.io.Serializable;


@Data
public class Person implements Serializable {
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