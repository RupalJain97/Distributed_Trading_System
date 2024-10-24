package com.trading.integrationTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PerformanceMetricsApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testPerformanceMetricsEndpoint() {
        // Call the /api/performance-metrics endpoint
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/performance-metrics", Map.class);

        // Validate response status
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Validate response body
        Map<String, Object> metrics = response.getBody();
        assertNotNull(metrics);

        // Check that all expected keys are present
        assertTrue(metrics.containsKey("totalThreads"));
        assertTrue(metrics.containsKey("activeOrderThreads"));
        assertTrue(metrics.containsKey("queueSize"));
        assertTrue(metrics.containsKey("completedTasks"));
        assertTrue(metrics.containsKey("rejectedTasks"));
        assertTrue(metrics.containsKey("jvmCpuLoad"));
        assertTrue(metrics.containsKey("systemCpuLoad"));
        assertTrue(metrics.containsKey("heapMemoryUsage"));
        assertTrue(metrics.containsKey("nonHeapMemoryUsage"));

        // Check that values are within expected ranges (for initial values)
        assertTrue((int) metrics.get("totalThreads") >= 0);
        assertTrue((int) metrics.get("activeOrderThreads") >= 0);
        assertTrue((int) metrics.get("queueSize") >= 0);
        assertTrue((int) metrics.get("completedTasks") >= 0);
        assertTrue((int) metrics.get("rejectedTasks") >= 0);
        assertTrue((double) metrics.get("jvmCpuLoad") >= 0.0);
        assertTrue((double) metrics.get("systemCpuLoad") >= 0.0);
        assertTrue((int) metrics.get("heapMemoryUsage") >= 0);
        assertTrue((int) metrics.get("nonHeapMemoryUsage") >= 0);

        System.out.println("Performance metrics API test passed.");
    }
}
