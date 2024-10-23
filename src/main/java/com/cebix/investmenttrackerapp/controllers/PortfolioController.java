package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.databaseutils.CustomUserDAO;
import com.cebix.investmenttrackerapp.databaseutils.CustomUserSessionFactory;
import com.cebix.investmenttrackerapp.databaseutils.PortfolioDAO;
import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioDAO portfolioDAO = new PortfolioDAO(CustomUserSessionFactory.getCustomUserSessionFactory());
    private final CustomUserDAO customUserDAO = new CustomUserDAO(CustomUserSessionFactory.getCustomUserSessionFactory());

    @GetMapping("/createPortfolio")
    public String showCreatePortfolioForm(Model model) {
        model.addAttribute("portfolio", new Portfolio());
        model.addAttribute("ticker", "");
        model.addAttribute("amount", 0);
        model.addAttribute("date", LocalDate.now());
        return "createPortfolio";
    }

    @PostMapping("/createPortfolio")
    public String createOrUpdatePortfolio(@RequestParam String ticker,
                                          @RequestParam int amount,
                                          Model model) {
        CustomUser loggedUser = getLoggedInUser();
        Portfolio existingPortfolio = portfolioDAO.findPortfolioByUserId(loggedUser.getId());

        Map<String, Integer> newStocks = new HashMap<>();
        newStocks.put(ticker, amount);

        if (existingPortfolio != null) {
            portfolioDAO.updatePortfolioStocksCollection(loggedUser.getId(), newStocks);
        } else {
            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setUser(loggedUser);
            newPortfolio.setStocks(newStocks);
            portfolioDAO.savePortfolio(newPortfolio);
        }

        return "portfolio";
    }

    private CustomUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            return customUserDAO.findUserByEmail(userDetails.getUsername());
        }

        throw new IllegalStateException("No user is currently logged in");
    }


}
