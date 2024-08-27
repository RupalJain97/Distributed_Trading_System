package com.threading.controller;

import com.threading.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class performanceController {
    @Autowired
    private FileProcessingService fileProcessingService;

    @GetMapping("/reading_performance")
    public ResponseEntity<Map<String, Object>> showPerformance(Model model) {
        fileProcessingService.processFileWithMultithread();
//        model.addAttribute("transactionCount", fileProcessingService.getTransactionCountRecord());
//        model.addAttribute("executionTime", fileProcessingService.getExecutionTime());
//        return "performance";
        Map<String, Object> metrics = new HashMap<>();
        long record_count = fileProcessingService.getTransactionCountRecord();
        long execution_time = fileProcessingService.getExecutionTime();
        metrics.put("transactionCount", record_count); // Replace with actual value
        metrics.put("executionTime", execution_time); // Replace with actual value
        System.out.println(record_count + " " + execution_time);
        return ResponseEntity.ok(metrics);
    }
}
