package io.beautifulfruit.finalproject.controller;

import java.util.*;

public class DatabaseController{
    
    static Map<String, User> testDatabase = new HashMap<String, User>(); 
    
    /**
     * If the user exists, update the user information,
     * or create a new user.
     *
     * @param user the user
     *
     * @return -1 if fails, otherwise 0
     */
    public static int updateUser(User user) {
        // TODO: implement it according to the description

        // this is an implement for testing purpose
        testDatabase.put(String.valueOf(user.id), user);
        return 0;
    }

    /**
     * Query user with specific indentifier, which may be the hash
     * of user information or something
     * 
     * @param identifier the identifier
     * 
     * @return a User object 
     */
    public static User queryUser(String identifier) {
        // TODO: implement it according to the description

        // this is an implement for testing purpose
        if (!isUserExists(identifier))
            return null;
        return testDatabase.get(identifier);
    }

    /**
     * Check if the user exists wit the identifier
     * 
     * @param identifier the identifier
     * 
     * @return @true if exists, otherwise @false
     */
    public static boolean isUserExists(String identifier) {
        // TODO: implement it according to the description

        // this is an implement for testing purpose
        return testDatabase.containsKey(identifier);
    }
}