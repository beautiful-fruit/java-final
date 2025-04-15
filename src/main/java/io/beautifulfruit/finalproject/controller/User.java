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

    /**
     * Add a new container to the User
     *
     * @param dockerCompose the docker compose plaintext file
     *
     * @return -1 if fail, otherwise 0
     */
    public int addContainer(String dockerCompose) {
        Container container = new Container(dockerCompose);
        if (container.status == Status.INVALID)
            return -1;
        containers.add(container);
        return 0;
    }

    /**
     * Get the number of containers
     *
     * @return an integer represents the number of containers
     */
    public int getContainerNumber() {
        return containers.size();
    }

    /**
     * Get the container with a specific index
     * This function won't check if the index is valid
     *
     * @param idx the index
     *
     * @return a Container object with respect to the index
     */
    public Container getContainer(int idx) {
        return containers.get(idx);
    }
}