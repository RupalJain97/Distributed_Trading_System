package com.trading.service;

import com.trading.model.UserModel;
import com.trading.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserModel validateUser(String userid, String password) {
        UserModel user = userRepository.findByUserid(userid);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Password matched: "+ user);
            return user;
        }
        return null;
    }

    public UserModel registerUser(String username, String userid, String password) throws Exception {
        if (userRepository.findByUserid(userid) != null) {
            throw new Exception("User ID already in use.");
        }
        UserModel user = new UserModel(username, userid, password);
        return userRepository.save(user);
    }

    public UserModel getUserByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }
}
