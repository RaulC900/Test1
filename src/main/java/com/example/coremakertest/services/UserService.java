package com.example.coremakertest.services;

import com.example.coremakertest.models.ReceivingMessageDTO;
import com.example.coremakertest.models.User;
import com.example.coremakertest.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Tuple;
import javax.swing.text.html.Option;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean addUser(User user) {
        if(isValid(user)){
            if(userRepository.findById(user.getUsername()).isEmpty()) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public ReceivingMessageDTO changePassword(String username, String oldPassword, String newPassword) {
        ReceivingMessageDTO rm = new ReceivingMessageDTO();
        rm.setBool(false);

        if(username == null) {
            rm.setMessage("User not found for the selected token");
            return rm;
        }

        Optional<User> user = userRepository.findById(username);
        if(user.isEmpty()) {
            rm.setMessage("User not found in the DB");
            return rm;
        }

        if(!bCryptPasswordEncoder.matches(oldPassword, user.get().getPassword())) {
            rm.setMessage("The old password you entered is incorrect");
            return rm;
        }

        if(newPassword == null || newPassword.isEmpty()) {
            rm.setMessage("New password is empty");
            return rm;
        }

        if(newPassword.equals(oldPassword)) {
            rm.setMessage("New password should be different from old password");
            return rm;
        }

        String oldP = user.get().getPassword();
        String newP = bCryptPasswordEncoder.encode(newPassword);
        user.get().setPassword(newP);
        userRepository.save(user.get());
        String message = "Password changed" + "\nOld Password: " + oldP + "\nNew Password: " + newP;
        rm.setMessage(message);
        rm.setBool(true);
        return rm;
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
        return user.getSurname() != null || user.getSurname().isEmpty();
    }

    public String login(String username, String password) {
        Optional<User> user = userRepository.findById(username);
        if(user.isEmpty()) {
            return null;
        }
        else if(bCryptPasswordEncoder.matches(password, user.get().getPassword())){
            return jwtUtil.generateToken(username);
        }
        else {
            return null;
        }
    }

    public User getUser(String username) {
        Optional<User> user = userRepository.findById(username);
        return user.orElse(null);
    }

    public String verifyToken(String token) {
        try {
            return jwtUtil.validateTokenAndRetrieveSubject(token);
        } catch (Exception ex) {
            return null;
        }
    }
}
