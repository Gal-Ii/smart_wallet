package app.transaction.repository;

import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
          SELECT COALESCE(SUM(t.amount), 0)
          FROM Transaction t
          """)
    BigDecimal getTotalTransactionsAmount();

    List<Transaction> findByType(TransactionType type);

    long countByType(TransactionType type);

    long countByStatus(TransactionStatus status);


    List<Transaction> findAllByOwnerId(UUID userId);
}
