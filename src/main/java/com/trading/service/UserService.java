package com.trading.service;

import com.trading.model.UserModel;
import com.trading.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // In-memory cache for users
    private final Map<String, UserModel> userCache = new ConcurrentHashMap<>();

    public UserModel validateUser(String userid, String password) {
        UserModel user = new UserModel();
        if (userCache.containsKey(userid)) {
            user = userCache.get(userid);
        } else {
            // Retrieve from database if not found in cache
            user = userRepository.findByUserid(userid);
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public UserModel registerUser(String username, String userid, String password) throws Exception {
        if (userCache.containsKey(userid)) {
            throw new Exception("User ID already in use.");
        }

        // Check database if not found in cache
        UserModel existingUser = userRepository.findByUserid(userid);
        if (existingUser != null) {
            throw new Exception("User ID already in use.");
        }

        UserModel user = new UserModel(username, userid, password);
        userRepository.save(user);

        // Update the cache
        userCache.put(userid, user);
        return user;
    }

    public UserModel getUserByUserid(String userid) {
        if (userCache.containsKey(userid)) {
            return userCache.get(userid);
        }

        // Retrieve from database if not found in cache
        UserModel user = userRepository.findByUserid(userid);
        if (user != null) {
            userCache.put(userid, user); // Update the cache
        }
        return user;
    }

    public boolean isUserExists(String userId) {
        return userCache.containsKey(userId) || userRepository.findByUserid(userId) != null;
    }

    public void clearUserCache() {
        userCache.clear();
        System.out.println("User cache cleared");
    }
}
