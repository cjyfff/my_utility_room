package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import com.example.Person;


@Controller
public class PersonController {
    @RequestMapping("/person")
    String person(Model model) {
        Person singlePerson = new Person("cjyfff", 29);

        List<Person> people = new ArrayList<>();
        Person p1 = new Person("aa", 11);
        Person p2 = new Person("bb", 22);
        Person p3 = new Person("cc", 33);

        people.add(p1);
        people.add(p2);
        people.add(p3);

        model.addAttribute("singlePerson", singlePerson);
        model.addAttribute("people", people);

        return "person";
    }
}