package com.trading.restAPITest;

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


@TestMethodOrder(OrderAnnotation.class)
class restAPITest {

// Registration & login
    @Test
    public void shouldRegisterUser() throws Exception {
        String registrationPayload = """
        {
            "username": "testuser",
            "email": "test@example.com",
            "password": "password123"
        }
    """;

        mockMvc.perform(post("/register")
                .content(registrationPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void shouldLoginUser() throws Exception {
        String loginPayload = """
        {
            "email": "test@example.com",
            "password": "password123"
        }
    """;

        mockMvc.perform(post("/login")
                .content(loginPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void shouldFetchUserProfile() throws Exception {
        mockMvc.perform(get("/user")
                .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    public void shouldLogoutUser() throws Exception {
        mockMvc.perform(get("/logout")
                .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

// Performance
    @Test
    public void shouldFetchCachedMetrics() throws Exception {
        mockMvc.perform(get("/dashboard/cached-metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalThreads").exists());
    }

    @Test
    public void shouldStreamLiveMetricsViaSSE() throws Exception {
        MvcResult result = mockMvc.perform(get("/dashboard/sse")
                .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
        assertTrue(responseContent.contains("totalThreads"));
    }

// Orders
    @Test
    public void shouldPlaceBuyOrder() throws Exception {
        String buyOrderPayload = """
        {
            "userId": 1,
            "stockSymbol": "GOOGL",
            "quantity": 5
        }
    """;

        mockMvc.perform(post("/stocks/buy")
                .content(buyOrderPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.totalPrice").exists());
    }

    @Test
    public void shouldPlaceSellOrder() throws Exception {
        String sellOrderPayload = """
        {
            "userId": 1,
            "stockSymbol": "AAPL",
            "quantity": 3
        }
    """;

        mockMvc.perform(post("/orders/sell")
                .content(sellOrderPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.remainingQuantity").exists());
    }

    @Test
    public void shouldFetchStocksHeldByUser() throws Exception {
        mockMvc.perform(get("/orders")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].stockSymbol").exists())
                .andExpect(jsonPath("$[0].quantity").exists());
    }

    @Test
    public void shouldHandleConcurrentOrders() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Runnable orderTask = () -> {
            String orderPayload = """
            {
                "userId": 1,
                "stockSymbol": "GOOGL",
                "quantity": 3,
                "orderType": "BUY"
            }
        """;

            try {
                mockMvc.perform(post("/orders")
                        .content(orderPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                fail("Concurrency test failed: " + e.getMessage());
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(orderTask);
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));
    }

// Stocks
    @Test
    public void shouldFetchAllAvailableStocks() throws Exception {
        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].symbol").exists())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    public void shouldUpdateStockPrice() throws Exception {
        String updatePayload = """
        {
            "stockId": 1,
            "newPrice": 150.75
        }
    """;

        mockMvc.perform(put("/stocks/update")
                .content(updatePayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(150.75));
    }

}
