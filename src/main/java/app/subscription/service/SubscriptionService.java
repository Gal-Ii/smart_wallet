package app.subscription.service;

import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.user.model.User;
import app.wallet.service.WalletService;
import app.web.dto.UpgradeRequest;
import jakarta.validation.Valid;
import jdk.jshell.JShell;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final WalletService walletService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, WalletService walletService) {
        this.subscriptionRepository = subscriptionRepository;
        this.walletService = walletService;
    }

    public void createDefaultSubscription(User user) {
        Subscription subscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .type(SubscriptionType.DEFAULT)
                .price(BigDecimal.ZERO)
                .renewalAllowed(true)
                .createdOn(LocalDateTime.now())
                .completedOn(LocalDateTime.now().plusMonths(1))
                .build();

        subscriptionRepository.save(subscription);
    }


    public Transaction upgrade(User user, @Valid UpgradeRequest upgradeRequest, SubscriptionType subscriptionType) {
        Optional<Subscription> currentlyActiveSubscriptionOpt = subscriptionRepository.findByStatusAndOwnerId(SubscriptionStatus.ACTIVE, user.getId());
        if(currentlyActiveSubscriptionOpt.isEmpty()){
            throw new RuntimeException("No active subscription not found for user with id [%s]".formatted(user.getId()));
        }

        Subscription curentlyActiveSubscription = currentlyActiveSubscriptionOpt.get();
        BigDecimal subscriptionPrice = getUpgradePrice(subscriptionType, upgradeRequest.getPeriod());
        //Upgrade request for Monthly (1 month) ULTIMATE
        String chargeDescription = "Upgrade request for %s %s".formatted(upgradeRequest.getPeriod().getDisplayName(), subscriptionType);
        Transaction chargeResultTransaction = walletService.withdrawal(user, upgradeRequest.getWalletId(), subscriptionPrice, chargeDescription);

        if(chargeResultTransaction.getStatus() == TransactionStatus.FAILED){
            return chargeResultTransaction;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryOn;

        if(upgradeRequest.getPeriod() == SubscriptionPeriod.MONTHLY){
            expiryOn = now.plusMonths(1).truncatedTo(ChronoUnit.DAYS);
        }else {
            expiryOn = now.plusYears(1).truncatedTo(ChronoUnit.DAYS);
        }

        //1. Create new active subscription.
        //2. Complete current active subscription.
        Subscription newActiveSubscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(upgradeRequest.getPeriod())
                .type(subscriptionType)
                .price(subscriptionPrice)
                .renewalAllowed(upgradeRequest.getPeriod() == SubscriptionPeriod.MONTHLY)
                .createdOn(now)
                .completedOn(expiryOn)
                .build();

        curentlyActiveSubscription.setStatus(SubscriptionStatus.COMPLETED);
        curentlyActiveSubscription.setCompletedOn(now);

        subscriptionRepository.save(curentlyActiveSubscription);
        subscriptionRepository.save(newActiveSubscription);

        return chargeResultTransaction;
    }

    private BigDecimal getUpgradePrice(SubscriptionType subscriptionType, SubscriptionPeriod period) {
        if(subscriptionType == SubscriptionType.DEFAULT){
            return BigDecimal.ZERO;
        } else if (subscriptionType == SubscriptionType.PREMIUM && period == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("19.99");
        }else if (subscriptionType == SubscriptionType.PREMIUM && period == SubscriptionPeriod.YEARLY) {
            return new BigDecimal("199.99");
        }else if (subscriptionType == SubscriptionType.ULTIMATE && period == SubscriptionPeriod.MONTHLY) {
            return new BigDecimal("49.99");
        }else if (subscriptionType == SubscriptionType.ULTIMATE && period == SubscriptionPeriod.YEARLY) {
            return new BigDecimal("499.99");
        }

        throw new RuntimeException("Price not found for type [%s] and period [%s]".formatted(subscriptionType, period));
    }
}
