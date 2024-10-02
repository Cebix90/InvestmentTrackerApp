package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.databaseutils.CustomUserDAO;
import com.cebix.investmenttrackerapp.databaseutils.CustomUserSessionFactory;
import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SecurityController {
    @GetMapping("/login")
    public String getLoginPage() {
        return "security/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("customUser", new CustomUser());
        return "security/register";
    }

    @PostMapping("/register")
    public String registerUser(CustomUser customUser) {
        CustomUserDAO dao = new CustomUserDAO(CustomUserSessionFactory.getCustomUserSessionFactory());
        dao.saveUser(customUser);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) throws ServletException
    {
        request.logout();
        return "redirect:/";
    }
}
