package com.trading.controller;

import com.trading.service.StockService;
import com.trading.model.StockModel;
import com.trading.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stocks")
    public String showStocks(Model model, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }

        model.addAttribute("user", user);
        List<StockModel> current_stocks = stockService.getAvailableStocks();
        model.addAttribute("stocks", current_stocks);
        return "stock_dashboard";
    }

    // Return stock data as JSON for polling
    @GetMapping("/stocks/data")
    @ResponseBody
    public List<StockModel> getStockData() {
        return stockService.getAvailableStocks(); // Return stock data as JSON
    }
}
