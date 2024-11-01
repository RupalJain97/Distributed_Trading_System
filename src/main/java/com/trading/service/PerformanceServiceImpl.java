package com.trading.service;

import io.grpc.stub.StreamObserver;
import com.trading.proto.PerformanceServiceGrpc;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.model.PerformanceMetrics;
import com.trading.proto.Performance.PerformanceRequest;
import com.trading.proto.Performance.PerformanceResponse;

@Component
public class PerformanceServiceImpl extends PerformanceServiceGrpc.PerformanceServiceImplBase {

    private final PerformanceMetricsService metricsService;

    @Autowired
    public PerformanceServiceImpl(PerformanceMetricsService metricsService) {
        this.metricsService = metricsService;
    }


    @Override 
    public void getPerformance(PerformanceRequest request, StreamObserver<PerformanceResponse> responseObserver) {
        // Calculate real performance metrics on Server Side (gRPC calls)
        PerformanceMetrics metrics = metricsService.calculatePerformanceMetrics();

        // Convert to gRPC response
        PerformanceResponse response = PerformanceResponse.newBuilder()
                .setTotalThreads(metrics.getTotalThreads())
                .setRunningThreads(metrics.getRunningThreads())
                .setPeakThreads(metrics.getPeakThreads())
                .setHeapMemoryUsage(metrics.getHeapMemoryUsage())
                .setNonHeapMemoryUsage(metrics.getNonHeapMemoryUsage())
                .setSystemLoad(metrics.getSystemLoad())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
