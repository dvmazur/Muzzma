package com.example.ivan.mrmuzzma;

public class Galary {

    private static int cursor;
    private static Person[] people;

    public Galary(){
        cursor = 0;
        people = new Person[]{
               new Person("KIZARU", "kizaru"),
                new Person("EMINEM", "eminem"),
                new Person("FRAY", "fray"),
                new Person("PUTIN", "putin")
        };
    }

    public Person getNext(){
        if (cursor == people.length - 1) cursor = -1;
        cursor++;
        Person cur = people[cursor];
        return cur;
    }

    public Person getCurrent(){
        return people[cursor];
    }

    public Person getPrev(){
        if (cursor == 0) cursor = people.length;
        cursor--;
        Person cur = people[cursor];
        return cur;
    }

}
