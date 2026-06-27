package app.web;

import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.model.UserRoles;
import app.user.repository.UserRepository;
import app.user.service.ReportService;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    public final ReportService reportService;
    public final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public ReportsController(ReportService reportService, UserService userService, TransactionService transactionService) {
        this.reportService = reportService;
        this.userService = userService;
        this.transactionService = transactionService;
    }


    @RequestMapping
    public ModelAndView getReportPage(){
        List<User> users = userService.getAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reports");
        modelAndView.addObject("totalUsers", reportService.getTotalUsers());
        modelAndView.addObject("activeUsers", reportService.getActiveUsers());
        modelAndView.addObject("inactiveUsers", reportService.getInactiveUsers());
        modelAndView.addObject("admins", reportService.getAdmins());
        modelAndView.addObject("nonAdmins", reportService.getNonAdmins());
        modelAndView.addObject("users", users);
        modelAndView.addObject("now", java.time.LocalDateTime.now());
        modelAndView.addObject("totallWallets", reportService.getTotalWallets());
        modelAndView.addObject("totallWalletAmount",reportService.getTotalWalletAmount());
        modelAndView.addObject("usersWithOneWallet",reportService.getUsersWithOneWallet());
        modelAndView.addObject("usersWithTwoWallets",reportService.getUsersWithTwoWallet());
        modelAndView.addObject("usersWithThreeWallets",reportService.getUsersWithThreeWallet());
        modelAndView.addObject("totalTransactions",reportService.getTotalTransactions());
        modelAndView.addObject("totalTransactionsAmount",reportService.getTotalTransactionsAmount());
        modelAndView.addObject("totalWithdrawals", reportService.getTotalWithdrawals());
        modelAndView.addObject("totalDeposits", reportService.getTotalDeposits());
        modelAndView.addObject("succeededTransactions", reportService.getTotalSucceededTransactions());
        modelAndView.addObject("failedTransactions", reportService.getTotalFailedTransactions());
        modelAndView.addObject("defaultSubscription", reportService.getTotalDefaultSubscriptions());
        modelAndView.addObject("premiumSubscription", reportService.getTotalPremiumSubscriptions());
        modelAndView.addObject("ultimateSubscription", reportService.getTotalUltimateSubscriptions());
        modelAndView.addObject("yearlySubscription", reportService.getTotalYearlySubscriptions());
        modelAndView.addObject("monthlySubscription", reportService.getTotalMonthlySubscriptions());

        return modelAndView;
    }
}
