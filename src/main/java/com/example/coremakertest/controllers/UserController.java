package com.example.coremakertest.controllers;

import com.example.coremakertest.models.User;
import com.example.coremakertest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Boolean> register(@RequestBody User user){
        Boolean isCreated = userService.addUser(user);
        if(isCreated) {
            return new ResponseEntity<Boolean>(isCreated, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if(token != null) {
            return new ResponseEntity<String>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/details")
    public ResponseEntity<String> getDetails(String username, String token) {
        if(userService.verifyToken(token)) {
            User user = userService.getUser(username);
            if(user != null) {
                return new ResponseEntity<String>(user.printDetails(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
