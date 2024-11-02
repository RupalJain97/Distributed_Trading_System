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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("/dashboard")
public class PerformanceController {

    @Autowired
    private PerformanceMetricsService metricsService;

    // List to manage active SseEmitters
    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock emitterLock = new ReentrantLock(); // Ensure single update at a time

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPerformanceMetrics() {
        System.out.println("SSE endpoint called");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed");
            removeEmitter(emitter);
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE connection timeout");
            removeEmitter(emitter);
        });

        emitter.onError((e) -> {
            System.err.println("SSE error: " + e.getMessage());
            removeEmitter(emitter);
        });

        return emitter;
    }

    private void removeEmitter(SseEmitter emitter) {
        synchronized (emitters) {
            emitters.remove(emitter);
        }
    }

    // Method to broadcast metrics to all connected clients
    public void broadcastMetrics(PerformanceMetrics metrics) {
        emitterLock.lock(); // Prevent simultaneous updates
        System.out.println("Emitters currently locked...");
        try {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(metrics);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.remove(emitter); // Remove failed emitter
                }
            }
        } finally {
            emitterLock.unlock(); // Release lock after updates
            System.out.println("Emitters currently Unlocked...");
        }
    }

    @PostConstruct
    public void startPeriodicMetricsUpdate() {
        ScheduledFuture<?> task = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                PerformanceMetrics metrics = metricsService.calculatePerformanceMetrics();
                System.out.println("Sending periodic metrics via SSE: " + metrics);
                broadcastMetrics(metrics);
            } catch (Exception e) {
                System.err.println("Error sending periodic SSE: " + e.getMessage());
            }
        }, 0, 3, TimeUnit.MINUTES); // Update every 3 minutes
    }
}
