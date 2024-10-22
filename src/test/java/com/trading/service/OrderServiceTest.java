package com.trading.service;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.model.UserHoldingsModel;
import com.trading.model.UserModel;
import com.trading.repository.OrderRepository;
import com.trading.repository.StockRepository;
import com.trading.repository.UserHoldingsRepository;
import com.trading.repository.UserRepository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


// Ensure a single instance is used for all tests in the class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Start a transaction for the entire test class
@Rollback(true) // Rollback all changes after the tests
@TestMethodOrder(OrderAnnotation.class)
class OrderServiceTest {

    @InjectMocks
    private TestOrderService orderService;

    @Mock
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserHoldingsRepository userHoldingsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static String userId = "Lauren";
    private static StockModel stock;
    private static UserHoldingsModel holdings;
    private static UserModel testuser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        stock = new StockModel("BLRK", "BlackRock Inc.", 100, 150.0);
        holdings = new UserHoldingsModel(1L, userId, "BLRK", "BlackRock Inc.", 10, 847.5f, null);

        // Mock user registration and repository interactions
        when(userRepository.findByUserid(userId)).thenReturn(testuser);
        when(stockRepository.findBySymbol("BLRK")).thenReturn(stock);
    }

    @Test
    @Order(1)
    public void testPlaceBuyOrderSuccess() {
        // Initialize common test data
        String username = "lauren";
        String password = "Password@user123";
        try {
            testuser = userService.registerUser(username, userId, password);
            when(userRepository.findByUserid(userId)).thenReturn(testuser);
            System.out.println("User created: " + userId);
        } catch (Exception e) {
            System.out.println("Error creating user " + userId + ": " + e.getMessage());
        }

        // Given
        OrderModel buyOrder = new OrderModel(userId, "BLRK", 10, "BlackRock Inc.", 847.5f, null, 8475.0f, "Buy");
        int expectedStockQuantity = stock.getQuantity() - buyOrder.getQuantity();

        // When
        orderService.placeBuyOrder(buyOrder, userId);
        System.out.println("Test Place Buy Order: Buy order placed successfully for user: " + userId);

        // Then
        assertEquals(expectedStockQuantity, stock.getQuantity()); // Stock quantity reduced

        // assertEquals(oldStockHoldingsQuantity + buyOrder.getQuantity(),
        // holdings.getCurrentQuantity());
        // verify(stockRepository, times(1)).save(stock);
        // verify(orderRepository, times(1)).save(buyOrder);
    }

    @Test
    @Order(2)
    public void testPlaceSellOrderSuccess() {
        // Given
        OrderModel sellOrder = new OrderModel(userId, "BLRK", 5, "BlackRock Inc.", 847.5f, null, 750.0f, "Sell");

        when(stockRepository.findBySymbol("BLRK")).thenReturn(stock);
        when(userHoldingsRepository.findByUserIdAndStockSymbol(userId, "BLRK")).thenReturn(Optional.of(holdings));
        int expectedStockQuantity = stock.getQuantity() + sellOrder.getQuantity();
        int expectedUserStockQuantity = holdings.getCurrentQuantity() - sellOrder.getQuantity();

        // When
        orderService.placeSellOrder(sellOrder, userId);
        System.out.println("Test Place Sell Order: Sell order placed successfully for user: " + userId);

        // Then
        assertEquals(expectedStockQuantity, stock.getQuantity()); // Stock quantity increased
        assertEquals(expectedUserStockQuantity, holdings.getCurrentQuantity()); // Holdings reduced
        // verify(stockRepository, times(1)).save(stock);
        // verify(orderRepository, times(1)).save(sellOrder);
        // verify(userHoldingsRepository, times(1)).save(holdings);
    }

    @Test
    @Order(3)
    public void testPlaceSellOrderFail_NoHoldings() {
        // Given
        OrderModel sellOrder = new OrderModel(userId, "CSCO", 10, "Cisco Systems Inc.", 51.3f, null, 513.0f, "Sell");

        
        when(stockRepository.findBySymbol("CSCO")).thenReturn(stock);
        when(userHoldingsRepository.findByUserIdAndStockSymbol(userId, "CSCO")).thenReturn(Optional.empty());

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeSellOrder(sellOrder, userId);
        });

        // Print Result
        System.out.println("Test Place Sell Order Failure: " + exception.getMessage());

        String expectedMessage = "Cannot sell stock user does not own.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Order(4)
    public void testPlaceBuyOrderFail_InsufficientStock() {
        // Given
        OrderModel buyOrder = new OrderModel(userId, "BLRK", 110, "BlackRock Inc.", 847.5f, null, 16500.0f, "Buy"); 

        when(stockRepository.findBySymbol("BLRK")).thenReturn(stock);

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeBuyOrder(buyOrder, userId);
        });

        // Print Result
        System.out.println("Test Place Buy Order Failure: " + exception.getMessage());

        String expectedMessage = "Insufficient stock quantity.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Order(5)
    public void testPlaceSellOrderFail_ExceedingHoldings() {
        // Given
        OrderModel sellOrder = new OrderModel(userId, "BLRK", 20, "BlackRock Inc.", 847.5f, null, 3000.0f, "Sell"); 

        when(stockRepository.findBySymbol("BLRK")).thenReturn(stock);
        when(userHoldingsRepository.findByUserIdAndStockSymbol(userId, "BLRK")).thenReturn(Optional.of(holdings));

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeSellOrder(sellOrder, userId);
        });

        // Print Result
        System.out.println("Test Place Sell Order Failure: " + exception.getMessage());

        String expectedMessage = "Cannot sell more than current holdings.";
        assertEquals(expectedMessage, exception.getMessage());
    }

}
