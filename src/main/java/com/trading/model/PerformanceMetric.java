package com.trading.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalThreads;
    private int runningThreads;
    private int peakThreads;
    private long heapMemoryUsage;
    private long nonHeapMemoryUsage;
    private double systemLoad;
    private LocalDateTime timestamp;

    public void setTotalThreads(int i) {
        this.totalThreads = i;
    }

    public void setHeapMemoryUsage(long l) {
        this.heapMemoryUsage = l;
    }

    public void setSystemLoad(double d) {
        this.systemLoad = d;
    }

    public void setRunningThreads(int r) {
        this.runningThreads = r;
    }

    public void setPeakThreads(int p) {
        this.peakThreads = p;
    }

    public void setNonHeapMemoryUsage(long n) {
        this.nonHeapMemoryUsage = n;
    }

    public void setTimestamp(LocalDateTime td) {
        this.timestamp = td;
    }
}
