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
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Test
    public void shouldHandleFullWorkflow() throws Exception {
        // Step 1: Create Stock
        String stockPayload = """
        {
            "symbol": "AAPL",
            "companyName": "Apple Inc.",
            "quantity": 1000,
            "price": 150.00
        }
    """;

        mockMvc.perform(post("/stocks")
                .content(stockPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Step 2: Fetch Stock Details
        MvcResult stockResult = mockMvc.perform(get("/stocks?symbol=AAPL"))
                .andExpect(status().isOk())
                .andReturn();

        String stockContent = stockResult.getResponse().getContentAsString();
        assertTrue(stockContent.contains("AAPL"));

        // Step 3: Place an Order
        String orderPayload = """
        {
            "userId": 1,
            "stockSymbol": "AAPL",
            "quantity": 5,
            "orderType": "BUY"
        }
    """;

        mockMvc.perform(post("/orders")
                .content(orderPayload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Step 4: Verify Stock Quantity Updates
        MvcResult updatedStockResult = mockMvc.perform(get("/stocks?symbol=AAPL"))
                .andExpect(status().isOk())
                .andReturn();

        String updatedStockContent = updatedStockResult.getResponse().getContentAsString();
        assertTrue(updatedStockContent.contains("\"quantity\":995"));

        // Step 5: Verify Performance Metrics Updates
        mockMvc.perform(get("/dashboard/cached-metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalThreads").exists());
    }

}
