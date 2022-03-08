package com.example.coremakertest.services;

import com.example.coremakertest.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Value("${jwt.token}")
    private String jwtSecret;

    private Map<String, User> users = new HashMap<>();

    public boolean addUser(User user) {
        if(isValid(user)){
            if(user.getPassword() != null) {
                users.put(user.getUsername(), user);
                return true;
                //hashMap doesn't allow duplicate keys - overwrites with new values
            }
        }
        return false;
    }

    private boolean isValid(User user) {
        if(user.getUsername() == null && !user.getUsername().isEmpty()) {
            return false;
        }
        if(user.getPassword() == null && !user.getPassword().isEmpty()) {
            return false;
        }
        if(user.getEmail() == null && !user.getEmail().isEmpty()) {
            return false;
        }
        if(user.getName() == null && !user.getName().isEmpty()) {
            return false;
        }
        if(user.getSurname() == null && !user.getSurname().isEmpty()) {
            return false;
        }
        return true;
    }

    public String login(String username, String password) {
        User user = users.get(username);
        if(user == null) {
            return null;
        }
        else if(user.getPassword().equals(password)) {
            return jwtSecret;
        }
        else {
            return null;
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean verifyToken(String token) {
        return jwtSecret.equals(token);
    }
}
