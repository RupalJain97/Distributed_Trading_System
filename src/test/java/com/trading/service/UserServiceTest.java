package com.trading.service;

import com.trading.model.UserModel;
import com.trading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

// Ensure a single instance is used for all tests in the class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Start a transaction for the entire test class
@Rollback(true) // Rollback all changes after the tests
@TestMethodOrder(OrderAnnotation.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Order(1)
    public void testRegisterUserSuccess() throws Exception {
        // Given
        String username = "John Doe";
        String userid = "john123";
        String password = "password123";
        UserModel user = new UserModel(username, userid, password);

        when(userRepository.findByUserid(userid)).thenReturn(null); // User not found
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        // When
        UserModel registeredUser = userService.registerUser(username, userid, password);

        // Print result
        System.out.println("Test Register User: User registered successfully - " + registeredUser.getUsername());

        // Then
        assertNotNull(registeredUser);
        assertEquals("john123", registeredUser.getUserid());
        assertEquals("John Doe", registeredUser.getUsername());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    @Order(2)
    public void testRegisterUserFailure_UserIDAlreadyInUse() {
        // Given
        String username = "John Doe";
        String userid = "john123";
        String password = "password123";
        UserModel existingUser = new UserModel(username, userid, password);

        when(userRepository.findByUserid(userid)).thenReturn(existingUser); // User already exists

        // When
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(username, userid, password);
        });

        // Print result
        System.out.println("Test Register User Failure: " + exception.getMessage());

        // Then
        String expectedMessage = "User ID already in use.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, never()).save(any(UserModel.class)); // Ensure no save attempt
    }

    @Test
    @Order(3)
    public void testLoginUserSuccess() {
        // Given
        String userid = "john123";
        String password = "password123";
        UserModel user = new UserModel("John Doe", userid, password);

        when(userRepository.findByUserid(userid)).thenReturn(user);

        // When
        UserModel validatedUser = userService.validateUser(userid, password);

        // Print result
        System.out.println("Test Validate User Login Success: User validated successfully - " + validatedUser.getUsername());

        // Then
        assertNotNull(validatedUser);
        assertEquals(userid, validatedUser.getUserid());
    }

    @Test
    @Order(4)
    public void testLoginUserFailure_WrongPassword() {
        // Given
        String userid = "john123";
        String password = "password123";
        UserModel user = new UserModel("John Doe", userid, password);

        when(userRepository.findByUserid(userid)).thenReturn(user);

        System.out.println("User password stored: " + user.getPassword() + " for userID: " + user.getUserid());
        // When
        UserModel validatedUser = userService.validateUser(userid, "wrongpassword");

        // Print result
        if (validatedUser == null) {
            System.out.println("Test Validate User Failure: Invalid password for user - " + userid);
        } else {
            System.out.println("Test Validate User Failure: User unexpectedly validated.");
        }

        // Then
        assertNull(validatedUser);
    }

    @Test
    @Order(5)
    public void testGetUserByUseridFailure_UserNotFound() {
        // Given
        String userid = "nonexistent_user";

        when(userRepository.findByUserid(userid)).thenReturn(null); // User not found

        // When
        UserModel fetchedUser = userService.getUserByUserid(userid);

        // Print result
        if (fetchedUser == null) {
            System.out.println("Test Get User by UserID Failure: No user found with userid - " + userid);
        } else {
            System.out.println("Test Get User by UserID Failure: Unexpectedly found user.");
        }

        // Then
        assertNull(fetchedUser);
    }
}
