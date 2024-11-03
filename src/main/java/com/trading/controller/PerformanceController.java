package com.trading.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.trading.service.PerformanceMetricsService;

import jakarta.annotation.PostConstruct;

import com.trading.model.PerformanceMetrics;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("/dashboard")
public class PerformanceController {

    @Autowired
    private PerformanceMetricsService metricsService;

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ReentrantLock emitterLock = new ReentrantLock();

    @GetMapping("/cached-metrics")
    public List<PerformanceMetrics> getCachedMetrics() {
        // Retrieve metrics for the last 24 hours from the service
        return metricsService.getMetricsFromCache();
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPerformanceMetrics() {
        System.out.println("SSE endpoint called");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String emitterId = UUID.randomUUID().toString();
        emitterMap.put(emitterId, emitter);

        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed");
            removeEmitter(emitterId);
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE connection timeout");
            removeEmitter(emitterId);
        });

        emitter.onError((e) -> {
            System.err.println("SSE error: " + e.getMessage());
            removeEmitter(emitterId);
        });

        return emitter;
    }

    private void removeEmitter(String emitterId) {
        // synchronized (emitters) {
            emitterMap.remove(emitterId);
        // }
    }

    // Method to broadcast metrics to all connected clients
    public void broadcastMetrics(PerformanceMetrics metrics) {
        emitterLock.lock(); // Prevent simultaneous updates
        System.out.println("Emitters currently locked...");
        try {
            // for (SseEmitter emitter : emitters) {
            //     try {
            //         emitter.send(metrics);
            //     } catch (IOException e) {
            //         emitter.completeWithError(e);
            //         removeEmitter(emitter);
            //     }
            // }
            emitterMap.values().removeIf(emitter -> {
                try {
                    emitter.send(metrics);
                    return false;
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    return true;  // Remove emitter on error
                }
            });
        } finally {
            emitterLock.unlock(); // Release lock after updates
            System.out.println("Emitters currently Unlocked...");
        }
    }

    @PostConstruct
    public void startPeriodicMetricsUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                PerformanceMetrics metrics = metricsService.calculatePerformanceMetrics();
                metricsService.saveToDatabase();
                System.out.println("Sending periodic metrics via SSE: " + metrics.toString());
                broadcastMetrics(metrics);
            } catch (Exception e) {
                System.err.println("Error sending periodic SSE: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.MINUTES); // Update every 5 minutes

        scheduler.scheduleAtFixedRate(() -> {
            metricsService.clearCache(); // Save cached data and clear it every 24 hours
        }, 0, 24, TimeUnit.HOURS);
    }
}
