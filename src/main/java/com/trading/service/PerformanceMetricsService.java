package com.trading.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.trading.model.PerformanceMetrics;
import com.trading.repository.PerformanceMetricsRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PerformanceMetricsService {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Autowired
    private PerformanceMetricsRepository metricsRepository;

    private final Map<Long, PerformanceMetrics> metricsCache = new ConcurrentHashMap<>();

    public List<PerformanceMetrics> getMetricsFromCache() {
        // Return metrics within the last 24 hours
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        return metricsCache.values().stream()
                .filter(metrics -> metrics.getTimestamp().getTime() >= twentyFourHoursAgo)
                .sorted(Comparator.comparing(PerformanceMetrics::getTimestamp))
                .collect(Collectors.toList());
    }

    public void cacheMetrics(PerformanceMetrics metrics) {
        metricsCache.put(System.currentTimeMillis(), metrics); // Cache latest metrics
    }

    public PerformanceMetrics calculatePerformanceMetrics() {
        PerformanceMetrics metrics = new PerformanceMetrics();

        // Thread metrics
        int totalThreads = threadMXBean.getThreadCount();
        int runningThreads = totalThreads - threadMXBean.getDaemonThreadCount();
        int peakThreads = threadMXBean.getPeakThreadCount();

        // Memory metrics
        long heapMemoryUsage = memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024);

        // CPU metrics
        double systemLoad = osMXBean.getSystemLoadAverage();

        metrics.setTotalThreads(totalThreads);
        metrics.setRunningThreads(runningThreads);
        metrics.setPeakThreads(peakThreads);
        metrics.setHeapMemoryUsage(heapMemoryUsage);
        metrics.setNonHeapMemoryUsage(nonHeapMemoryUsage);
        metrics.setSystemLoad(systemLoad);
        metrics.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // Cache the latest metrics
        cacheMetrics(metrics);

        return metrics;
    }

    // @Scheduled(fixedRate = 300000) // Update the database every 5 minutes
    public void saveToDatabase() {
        List<PerformanceMetrics> metricsToSave = new ArrayList<>(metricsCache.values());
        metricsRepository.saveAll(metricsToSave);
        System.out.println("Cached metrics persisted to the database");
        // Remove outdated cache entries (older than 24 hours)
        // metricsCache.entrySet().removeIf(entry -> System.currentTimeMillis()
        //         - entry.getValue().getTimestamp().getTime() > 24 * 60 * 60 * 1000);
    }

    // @Scheduled(fixedRate = 86400000) // Update the database every 24 hours
    public void clearCache() {
        saveToDatabase();
        System.out.println("Cached metrics persisted to the database and cleared");
        metricsCache.clear();
    }

    // Fetch the latest cached metrics
    public PerformanceMetrics fetchLatestMetricsFromCache() {
        return metricsCache.values().stream()
                .max(Comparator.comparing(PerformanceMetrics::getTimestamp))
                .orElse(calculatePerformanceMetrics());
    }
}
