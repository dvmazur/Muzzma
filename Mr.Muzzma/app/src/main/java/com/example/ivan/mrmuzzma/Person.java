package com.example.ivan.mrmuzzma;

import android.graphics.drawable.Drawable;

public class Person {

    private String drawable;
    private String  name;

    public Person(String name, String drawable){
        this.name = name;
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public String getDrawable() {
        return drawable;
    }

}
