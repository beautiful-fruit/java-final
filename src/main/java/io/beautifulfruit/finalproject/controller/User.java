package io.beautifulfruit.finalproject.controller;

import java.util.ArrayList;
import java.util.List;


public class User {
    int id;
    int quota;
    private List<Container> containers;

    public User(int id, int quota) {
        this.id = id;
        this.quota = quota;
        this.containers = new ArrayList<Container>();
    }

}