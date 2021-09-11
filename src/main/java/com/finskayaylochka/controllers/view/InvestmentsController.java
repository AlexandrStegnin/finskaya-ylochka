package com.finskayaylochka.controllers.view;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.supporting.dto.BalanceDTO;
import com.finskayaylochka.service.AccountTransactionService;
import com.finskayaylochka.service.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

/**
 * @author Alexandr Stegnin
 */

@Controller
public class InvestmentsController {

    private final AccountTransactionService accountTransactionService;

    private final AppUserService appUserService;

    public InvestmentsController(AccountTransactionService accountTransactionService,
                                 AppUserService appUserService) {
        this.accountTransactionService = accountTransactionService;
        this.appUserService = appUserService;
    }

    @PostMapping(path = "/investments")
    public String showInvestments(@ModelAttribute AppUser user, ModelMap model) {
        String login = user.getLogin();
        model.addAttribute("investorLogin", login);
        AppUser investor = appUserService.findByLogin(login);
        Long ownerId = investor.getId();
        BalanceDTO balanceDTO = accountTransactionService.getBalance(ownerId);
        BigDecimal balance = balanceDTO.getSummary();
        if (balance.compareTo(BigDecimal.valueOf(-1)) > 0 && balance.compareTo(BigDecimal.ONE) < 0) {
            balance = BigDecimal.ZERO;
        }
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("balance", balance);
        return "/flows";
    }

}
