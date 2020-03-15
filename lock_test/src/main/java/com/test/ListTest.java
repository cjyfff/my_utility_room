package com.test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i ++) {
            A a = new A();
            a.run();
            a.get_l();
        }
    }
}

class A {
    List<String> l = new ArrayList<>();

    public void run() {
        this.l.add("a");
    }

    public void get_l() {
        System.out.println(this.l);
    }
}
