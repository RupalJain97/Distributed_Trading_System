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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


@RestController
@RequestMapping("/dashboard")
public class PerformanceController {

    @Autowired
    private PerformanceMetricsService metricsService;

    // List to manage active SseEmitters
    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPerformanceMetrics() {
        System.out.println("SSE endpoint called");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // ScheduledFuture<?> task = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
        //     try {
        //         if (metricsService != null) {
        //             System.out.println("Calculating performance metrics...");
        //             PerformanceMetrics metrics = metricsService.calculatePerformanceMetrics();
        //             System.out.println("Sending metrics via SSE: " + metrics);
        //             emitter.send(metrics);
        //         } else {
        //             System.err.println("PerformanceMetricsService is null!");
        //         }
        //     } catch (IOException e) {
        //         System.err.println("Error sending SSE: " + e.getMessage());
        //         emitter.completeWithError(e);
        //     } catch (Exception e) {
        //         System.err.println("Unexpected error in SSE: " + e.getMessage());
        //         emitter.completeWithError(e);
        //     }
        // }, 0, 20, TimeUnit.SECONDS);

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
        synchronized (emitters) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(metrics);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }

    @PostConstruct
    public void startPeriodicMetricsUpdate() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                PerformanceMetrics metrics = metricsService.calculatePerformanceMetrics();
                System.out.println("Sending periodic metrics via SSE: " + metrics);
                broadcastMetrics(metrics);
            } catch (Exception e) {
                System.err.println("Error sending periodic SSE: " + e.getMessage());
            }
        }, 0, 20, TimeUnit.SECONDS); // Update every 5 minutes
    }
}
