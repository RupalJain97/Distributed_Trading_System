package com.trading.model;

import org.springframework.format.annotation.DateTimeFormat;

public class PerformanceMetrics {
    private int totalThreads;
    private int runningThreads;
    private int peakThreads;
    private long heapMemoryUsage;
    private long nonHeapMemoryUsage;
    private double systemLoad;
    private DateTimeFormat systemTime;

    // Getters and setters
    public int getTotalThreads() {
        return totalThreads;
    }

    public void setTotalThreads(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public int getRunningThreads() {
        return runningThreads;
    }

    public void setRunningThreads(int runningThreads) {
        this.runningThreads = runningThreads;
    }

    public int getPeakThreads() {
        return peakThreads;
    }

    public void setPeakThreads(int peakThreads) {
        this.peakThreads = peakThreads;
    }

    public long getHeapMemoryUsage() {
        return heapMemoryUsage;
    }

    public void setHeapMemoryUsage(long heapMemoryUsage) {
        this.heapMemoryUsage = heapMemoryUsage;
    }

    public long getNonHeapMemoryUsage() {
        return nonHeapMemoryUsage;
    }

    public void setNonHeapMemoryUsage(long nonHeapMemoryUsage) {
        this.nonHeapMemoryUsage = nonHeapMemoryUsage;
    }

    public double getSystemLoad() {
        return systemLoad;
    }

    public void setSystemLoad(double systemLoad) {
        this.systemLoad = systemLoad;
    }

    public DateTimeFormat getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(DateTimeFormat systemTime) {
        this.systemTime = systemTime;
    }
}
