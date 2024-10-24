package com.trading.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@Service
public class PerformanceService {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

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

        return metrics.toString();
    }
}
