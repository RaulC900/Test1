package com.example.coremakertest.controllers;

import com.example.coremakertest.models.ReceivingMessageDTO;
import com.example.coremakertest.models.User;
import com.example.coremakertest.models.UserPasswordChangeDTO;
import com.example.coremakertest.services.UserService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> register(@RequestBody User user){
        Boolean isCreated = userService.addUser(user);
        if(isCreated) {
            return new ResponseEntity<>(isCreated, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        if(token != null) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/details")
    @ResponseBody
    public ResponseEntity<String> getDetails(@RequestHeader HttpHeaders headers) throws JsonProcessingException {
        String token;
        try {
            token = headers.getFirst(HttpHeaders.AUTHORIZATION);
            token = token.replace("Bearer ", "");
        }
        catch (Exception ex) {
            return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
        }
        String returnedUsername = userService.verifyToken(token);
        User user = userService.getUser(returnedUsername);
        if(user != null) {
            FilterProvider filters = new SimpleFilterProvider() .addFilter(
                    "user_filter", SimpleBeanPropertyFilter.serializeAllExcept("password"));
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writer(filters)
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(user);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestHeader HttpHeaders headers, @RequestBody UserPasswordChangeDTO upc) {
        String token;
        try {
            token = headers.getFirst(HttpHeaders.AUTHORIZATION);
            token = token.replace("Bearer ", "");
        }
        catch (Exception ex) {
            return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
        }
        String returnedUsername = userService.verifyToken(token);
        ReceivingMessageDTO rm = userService.changePassword(returnedUsername, upc.getOldPassword(), upc.getNewPassword());
        if(rm.getBool()) {
            return new ResponseEntity<>(rm.getMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>(rm.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
