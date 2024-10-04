package com.trading.controller;

import com.trading.model.OrderModel;
import com.trading.model.UserModel;
import com.trading.service.OrderService;
import com.trading.service.StockService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
// @RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String renderOrderProcessingPage(Model model, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }

        List<OrderModel> orders = orderService.getUserOrders(user.getUserid());
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        return "orderProcessing";
    }

    @PostMapping("/stocks/buy")
    @ResponseBody
    public String placeBuyOrder(@RequestBody OrderModel order, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }
        System.out.println("Placing Buy Stocks Order..." + order.toString());
        orderService.placeBuyOrder(order, user.getUserid());
        return "Buy order placed for " + order.getStockSymbol() + " with quantity: " + order.getQuantity();
    }

    @PostMapping("/orders/sell")
    @ResponseBody
    public String placeSellOrder(@RequestBody OrderModel order, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }
        System.out.println("Selling Stocks..." + order.toString());
        orderService.placeSellOrder(order, user.getUserid());
        return "Sell order placed for " + order.getStockSymbol();
    }

    @GetMapping("/orders/{userId}")
    @ResponseBody
    public String getOrderStatus(@PathVariable String userId) {
        return "User " + userId + " has placed " + orderService.getOrderCount() + " orders.";
    }

    @GetMapping("/{userId}/history")
    public String showOrderHistory(Model model, HttpSession session) {
        UserModel user = (UserModel) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if session expires
        }

        List<OrderModel> orderHistory = orderService.getUserOrderHistory(user.getUserid());
        model.addAttribute("orderHistory", orderHistory);
        model.addAttribute("user", user);
        return "userOrderHistory"; // Return order history page
    }
}
