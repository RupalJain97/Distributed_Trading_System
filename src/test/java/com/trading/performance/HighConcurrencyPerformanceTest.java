package com.trading.performance;

import com.trading.model.OrderModel;
import com.trading.model.UserModel;
import com.trading.repository.UserRepository;
import com.trading.service.OrderService;
import com.trading.service.TestOrderService;
import com.trading.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
// Ensure a single instance is used for all tests in the class
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional // Start a transaction for the entire test class
@Rollback(true) // Rollback all changes after the tests
public class HighConcurrencyPerformanceTest {

    @Autowired
    @InjectMocks
    private OrderService orderService;

    @Autowired
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {

        userService.clearUserCache();
        for (int i = 0; i < 15; i++) {
            String username = "Customer " + i;
            String userid = "cust" + i;
            String password = "Password@user123";

            try {
                userService.registerUser(username, userid, password);
                System.out.println("User created: " + userid);
            } catch (Exception e) {
                System.out.println("Error creating user " + userid + ": " + e.getMessage());
            }
        }

    }

    @Test
    public void testHighConcurrencyUserCreationAndBuyOrder() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(20); 

        // Simulate concurrent creation of 15 users and their buy orders
        for (int i = 0; i < 15; i++) {
            int finalI = i; // Needed for lambda expression
            executorService.submit(() -> {
                String userid = "cust" + finalI;

                try {
                    OrderModel order = new OrderModel(userid, "ADBE", 10, "Adobe Inc.", 630.5f, null, 6305.0f, "Buy");
                    orderService.placeBuyOrder(order, userid);
                    System.out.println("Thread " + Thread.currentThread().getName() + ": " + userid + " buying ADBE");

                } catch (RuntimeException e) {
                    System.out.println("Error for user " + userid + ": " + e.getMessage());
                }
            });
        }

        // Shutdown the executor and wait for all tasks to finish
        executorService.shutdown();
        if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Some tasks did not finish within the timeout!");
        } else {
            System.out.println("High Concurrency Test: All tasks completed");
        }

        // Add a delay to ensure the database updates are committed
        Thread.sleep(2000);

        // Verify user holdings after test
        UserModel user0 = userRepository.findByUserid("cust0");
        if (user0 != null) {
            System.out.println(
                    "Stock quantity for user0 after high load: "
                            + orderService.getUserStocksByUserId(user0.getUserid()));
        } else {
            System.out.println("User0 not found in repository.");
        }
    }

}
