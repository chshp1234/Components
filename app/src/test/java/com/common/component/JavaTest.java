package com.common.component;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 created by dongdaqing 2022/4/26 10:19
 */
public class JavaTest {

    @Test
    public void test() {
        List<Cat> cats = new ArrayList<>();
        List<Dog> dogs = new ArrayList<>();
        List<Animal> animals = new ArrayList<>();
        List<Object> objects = new ArrayList<>();

        List<? extends Animal> animals1 = cats;
        animals.add(null);
        Animal animal = animals.get(0);

        List<? super Animal> animals2 = objects;
        animals2.add(new Animal());
        animals2.add(new Cat());
        animals2.add(new Dog());
        animals2.add(new Dog());
        Object object = animals2.get(0);
    }

    class Animal {}

    class Cat extends Animal {}

    class Dog extends Animal {}
}
