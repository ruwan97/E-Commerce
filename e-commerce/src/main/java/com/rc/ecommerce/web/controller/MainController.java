package com.rc.ecommerce.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class MainController {

    @RequestMapping("/")
    public RedirectView redirectToLogin() {
        return new RedirectView("/login");
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "auth/sign-in";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "auth/sign-up";
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard/dashboard";
    }

    @GetMapping("/sidebar")
    public String getSidebar() {
        return "partials/sidebar";
    }

    @GetMapping("/header")
    public String getHeader() {
        return "partials/header";
    }

    @GetMapping("/footer")
    public String getFooter() {
        return "partials/footer";
    }

    @GetMapping("/dash-content")
    public String getDashboardContent() {
        return "partials/dashboard-content";
    }

    @GetMapping("/reset-password")
    public String getResetPassword() {
        return "auth/reset-password";
    }
}
