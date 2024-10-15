package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.databaseutils.CustomUserDAO;
import com.cebix.investmenttrackerapp.databaseutils.CustomUserSessionFactory;
import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.dtos.RegisterUserDTO;
import com.cebix.investmenttrackerapp.exceptions.UserAlreadyExistsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SecurityController {
    private final CustomUserDAO dao = new CustomUserDAO(CustomUserSessionFactory.getCustomUserSessionFactory());

    @GetMapping("/login")
    public String getLoginPage() {
        return "security/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUserDTO", new RegisterUserDTO());
        return "security/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterUserDTO registerUserDTO, final BindingResult bindingResult, final Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerUserDTO", registerUserDTO);
            return "security/register";
        }

        try {
            CustomUser customUser = new CustomUser();
            customUser.setEmail(registerUserDTO.getEmail());
            customUser.setPassword(registerUserDTO.getPassword());

            dao.saveUser(customUser);
        } catch (UserAlreadyExistsException e) {
            bindingResult.rejectValue("email", "customUser.email", "Email already in use. Please try different email.");
            model.addAttribute("registerUserDTO", registerUserDTO);
            return "security/register";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }
}
