package com.trading;

import com.trading.service.GrpcServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TradingSystem {

    public static void main(String[] args) {
        SpringApplication.run(TradingSystem.class, args);
    }

    @Bean
    public CommandLineRunner startGrpcServer(GrpcServer grpcServer) {
        return args -> {
            try {
                grpcServer.start(9090); // Start the gRPC server
                grpcServer.blockUntilShutdown(); // Keep the server running until shutdown
            } catch (Exception e) {
                System.err.println("Error starting gRPC server: " + e.getMessage());
            }
        };
    }

}
