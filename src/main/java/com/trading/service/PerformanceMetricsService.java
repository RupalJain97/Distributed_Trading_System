package com.trading.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import org.springframework.stereotype.Service;
import com.trading.model.PerformanceMetrics;

@Service
public class PerformanceMetricsService {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

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

        return metrics;
    }
}
