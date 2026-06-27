package app.user.service;

import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.repository.TransactionRepository;
import app.user.model.UserRoles;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ReportService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public ReportService(UserRepository userRepository, WalletRepository walletRepository, SubscriptionRepository subscriptionRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.transactionRepository = transactionRepository;
    }

    public long getTotalUsers(){return userRepository.count();}

    public long getActiveUsers(){
        return userRepository.countByActiveTrue();
    }

    public long getInactiveUsers(){
        return userRepository.countByActiveFalse();
    }

    public long getAdmins(){
        return userRepository.countByRole(UserRoles.ADMIN);
    }

    public long getNonAdmins(){
        return userRepository.countByRole(UserRoles.USER);
    }

    public long getTotalWallets(){return walletRepository.count();}

    public BigDecimal getTotalWalletAmount(){
        return walletRepository.getTotalWalletAmount();
    };

    public double getUsersWithOneWallet(){
        return getUsersWithWalletCountPercentage(1);
    }

    public double getUsersWithTwoWallet(){
        return getUsersWithWalletCountPercentage(2);
    }

    public double getUsersWithThreeWallet(){
        return getUsersWithWalletCountPercentage(3);
    }

    private double getUsersWithWalletCountPercentage(int walletCount){
        long totalUsers = userRepository.count();
        if(totalUsers == 0){
            return 0.00;
        }

        long usersWithWalletCount = userRepository.countUsersByWalletCount(walletCount);

        return (usersWithWalletCount*100.0)/totalUsers;
    }

    public long getTotalTransactions(){
        return transactionRepository.count();
    }

    public BigDecimal getTotalTransactionsAmount(){
        return transactionRepository.getTotalTransactionsAmount();
    }

    public long getTotalWithdrawals(){
        return transactionRepository.countByType(TransactionType.WITHDRAWAL);
    }

    public long getTotalDeposits(){
        return transactionRepository.countByType(TransactionType.DEPOSIT);
    }

    public long getTotalSucceededTransactions(){
        return transactionRepository.countByStatus(TransactionStatus.SUCCEEDED);
    }

    public long getTotalFailedTransactions(){
        return transactionRepository.countByStatus(TransactionStatus.FAILED);
    }

    public long getTotalDefaultSubscriptions(){
        return subscriptionRepository.countSubscriptionsByType(SubscriptionType.DEFAULT);
    }

    public long getTotalPremiumSubscriptions(){
        return subscriptionRepository.countSubscriptionsByType(SubscriptionType.PREMIUM);
    }

    public long getTotalUltimateSubscriptions(){
        return subscriptionRepository.countSubscriptionsByType(SubscriptionType.ULTIMATE);
    }


    public long getTotalMonthlySubscriptions(){
        return subscriptionRepository.countSubscriptionsByPeriod(SubscriptionPeriod.MONTHLY);
    }

    public long getTotalYearlySubscriptions(){
        return subscriptionRepository.countSubscriptionsByPeriod(SubscriptionPeriod.YEARLY);
    }

}
