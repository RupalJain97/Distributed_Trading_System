package com.trading.model;

import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.sql.Timestamp;

@Entity
public class PerformanceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logid; // Auto-generated id

    @Column(name = "totalThreads", nullable = false)
    private int totalThreads;

    @Column(name = "runningThreads", nullable = false)
    private int runningThreads;

    @Column(name = "peakThreads", nullable = false)
    private int peakThreads;

    @Column(name = "heapMemoryUsage", nullable = false)
    private long heapMemoryUsage;

    @Column(name = "nonHeapMemoryUsage", nullable = false)
    private long nonHeapMemoryUsage;

    @Column(name = "systemLoad", nullable = false)
    private double systemLoad;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PerformanceModel: {" +
                "totalThreads='" + totalThreads + '\'' +
                ", runningThreads='" + runningThreads + '\'' +
                ", peakThreads=" + peakThreads +
                ", heapMemoryUsage=" + heapMemoryUsage +
                ", nonHeapMemoryUsage=" + nonHeapMemoryUsage +
                ", systemLoad=" + systemLoad +
                ", timestamp=" + timestamp +
                '}';
    }
}
