package io.beautifulfruit.finalproject.controller;

public class Container {
    Status status;
    String dockerCompose;

    // TODO: Add more necessary property

    public Container(String dockerCompose) {
        this.dockerCompose = dockerCompose;
        // do something
        this.status = Status.READY;
    }
}