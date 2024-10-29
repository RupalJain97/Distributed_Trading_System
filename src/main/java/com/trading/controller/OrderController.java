package com.trading.controller;

import com.trading.model.OrderModel;
import com.trading.model.UserHoldingsModel;
import com.trading.model.UserModel;
import com.trading.service.OrderService;
import com.trading.service.StockService;
// import com.trading.service.UserHoldingsService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/orders")
    public String renderOrderProcessingPage(Model model, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }
        
        List<UserHoldingsModel> holdings = orderService.getUserStocksByUserId(user.getUserid());
        model.addAttribute("orders", holdings);
        model.addAttribute("user", user);
        return "orderProcessing";
    }

    @PostMapping("/stocks/buy")
    @ResponseBody
    public String placeBuyOrder(@RequestBody OrderModel order, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        // if (user == null) {
        //     return "redirect:/login"; // Redirect to login if session expires
        // }
        
        orderService.placeBuyOrder(order, user.getUserid());
        return "Buy order placed for " + order.getStockSymbol() + " with quantity: " + order.getQuantity();
    }

    @PostMapping("/orders/sell")
    @ResponseBody
    public String placeSellOrder(@RequestBody OrderModel order, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        // if (user == null) {
        //     return "redirect:/login"; // Redirect to login if session expires
        // }
        
        orderService.placeSellOrder(order, user.getUserid());
        return "Sell order placed for " + order.getStockSymbol();
    }

    @GetMapping("/orders/{userId}")
    @ResponseBody
    public String getOrderStatus(@PathVariable String userId) {
        return " "+ orderService.getOrderCount(userId) + " ";
    }

    @GetMapping("/{userId}/history")
    public String showOrderHistory(Model model, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }

        List<OrderModel> orderHistory = orderService.getOrderHistoryByUserId(user.getUserid());
        model.addAttribute("orderHistory", orderHistory);
        model.addAttribute("user", user);
        return "userOrderHistory"; // Return order history page
    }
}
