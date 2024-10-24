package com.trading.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.model.PerformanceMetric;
// import com.trading.repository.PerformanceMetricRepository;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class PerformanceService {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    // @Autowired
    // private PerformanceMetricRepository metricRepository;

    // public void saveMetrics(JSONObject metrics) {
    //     PerformanceMetric metric = new PerformanceMetric();
    //     metric.setTotalThreads((int) metrics.get("totalThreads"));
    //     metric.setRunningThreads((int) metrics.get("runningThreads"));
    //     metric.setPeakThreads((int) metrics.get("peakThreads"));
    //     metric.setHeapMemoryUsage((long) metrics.get("heapMemoryUsage"));
    //     metric.setNonHeapMemoryUsage((long) metrics.get("nonHeapMemoryUsage"));
    //     metric.setSystemLoad((double) metrics.get("systemLoad"));
    //     metric.setTimestamp(LocalDateTime.now());

    //     metricRepository.save(metric);
    // }

    public String getPerformanceMetrics() {
        JSONObject metrics = new JSONObject();
        
        // Thread metrics
        int totalThreads = threadMXBean.getThreadCount();
        int runningThreads = threadMXBean.getThreadCount() - threadMXBean.getDaemonThreadCount();
        int peakThreads = threadMXBean.getPeakThreadCount();

        // Memory metrics
        long heapMemoryUsage = memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024);

        // CPU metrics
        double systemLoad = osMXBean.getSystemLoadAverage();
        
        metrics.put("totalThreads", totalThreads);
        metrics.put("runningThreads", runningThreads);
        metrics.put("peakThreads", peakThreads);
        metrics.put("heapMemoryUsage", heapMemoryUsage);
        metrics.put("nonHeapMemoryUsage", nonHeapMemoryUsage);
        metrics.put("systemLoad", systemLoad);

        // saveMetrics(metrics);
        return metrics.toString();
    }
}
