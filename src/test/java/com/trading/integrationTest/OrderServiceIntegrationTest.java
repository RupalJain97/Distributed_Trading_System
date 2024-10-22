package com.trading.integrationTest;

import com.trading.model.OrderModel;
import com.trading.model.StockModel;
import com.trading.model.UserModel;
import com.trading.repository.UserRepository;
import com.trading.service.OrderService;
import com.trading.service.StockService;
import com.trading.service.TestOrderService;
import com.trading.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.*;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRED)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
// Ensure a single instance is used for all tests in the class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Rollback(true) // Rollback all changes after the tests
public class OrderServiceIntegrationTest {

    @Autowired
    private TestOrderService orderService;

    @Autowired
    private StockService stockService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        stockService.clearStockCache();
        try {
            userService.registerUser("Marry Doughlas", "marry", "password123");
        } catch (Exception e) {
            System.out.println("User already exists.");
        }
    }

    @Test
    @Order(1)
    @Rollback(false)
    @Commit
    public void testPlaceBuyOrder() {
        // Create a user
        // try {
        // userService.registerUser("Marry Doughlas", "marry", "password123");
        // System.out.println("Test User Registered");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // Initial stock quantity
        StockModel stockBefore = stockService.findStockBySymbol("AAPL");
        int initialQuantity = stockBefore.getQuantity();
        System.out.println("Initial AAPL Stock Quantity (Buy): " + initialQuantity);

        // Place a buy order
        OrderModel order = new OrderModel("marry", "AAPL", 10, "Apple", 150.0f, null, 1500.0f, "Buy");
        orderService.placeBuyOrder(order, "marry");

        // Validate stock quantity is reduced by the order quantity
        StockModel stockAfter = stockService.findStockBySymbol("AAPL");
        int expectedQuantity = initialQuantity - 10;
        System.out.println("Final AAPL Stock Quantity (Buy): " + stockAfter.getQuantity());
        assertEquals(expectedQuantity, stockAfter.getQuantity());
    }

    @Test
    @Order(2)
    @Rollback(false)
    @Commit
    public void testPlaceSellOrder() {
        // Initial stock quantity
        StockModel stockBefore = stockService.findStockBySymbol("AAPL");
        int initialQuantity = stockBefore.getQuantity();
        System.out.println("Initial AAPL Stock Quantity (Sell): " + initialQuantity);

        // Place a sell order
        OrderModel order = new OrderModel("marry", "AAPL", 5, "Apple", 150.0f, null, 750.0f, "Sell");
        orderService.placeSellOrder(order, "marry");

        // Validate stock quantity is increased by the order quantity
        StockModel stockAfter = stockService.findStockBySymbol("AAPL");
        int expectedQuantity = initialQuantity + 5;
        System.out.println("Final AAPL Stock Quantity (Sell): " + stockAfter.getQuantity());
        assertEquals(expectedQuantity, stockAfter.getQuantity());
    }

    @Test
    @Order(3)
    public void testPlaceBuyOrder_UserNotExists() {
        OrderModel order = new OrderModel("nonexistentUser", "AAPL", 10, "Apple", 150.0f, null, 1500.0f, "Buy");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeBuyOrder(order, "nonexistentUser");
        });

        String expectedMessage = "User does not exist. Cannot place order.";
        String actualMessage = exception.getMessage();

        System.out.println("Message received: " + actualMessage);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @Order(4)
    public void testPlaceSellOrder_UserNotExists() {
        OrderModel order = new OrderModel("nonexistentUser", "AAPL", 5, "Apple", 150.0f, null, 750.0f, "Sell");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeSellOrder(order, "nonexistentUser");
        });

        String expectedMessage = "User does not exist. Cannot place order.";
        String actualMessage = exception.getMessage();

        System.out.println("Message received: " + actualMessage);
        assertEquals(expectedMessage, actualMessage);
    }
}
