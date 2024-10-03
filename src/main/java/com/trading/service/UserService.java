package com.trading.service;

import com.trading.model.UserModel;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    // Simple user store (replace with DB later)
    private Map<String, UserModel> users = new HashMap<>();

    public UserModel validateUser(String userid, String password) {
        UserModel user = users.get(userid);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public UserModel registerUser(String username, String userid, String password) {
        UserModel user = new UserModel(username, userid, password);
        users.put(userid, user);
        return user;
    }
}
