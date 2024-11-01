package com.trading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@Component
public class GrpcServer {

    private Server server;

    private final PerformanceMetricsService metricsService;

    @Autowired
    public GrpcServer(PerformanceMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    public void start(int port) throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new PerformanceServiceImpl(metricsService)) 
                .build()
                .start();
        System.out.println("gRPC server started, listening on " + port);

        // Add a shutdown hook to stop the server when the application stops
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server");
            GrpcServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
