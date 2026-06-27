package app.user.repository;

import app.user.model.User;
import app.user.model.UserRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    Optional <User> findByUsername(String username);

    long countByRole(UserRoles role);

    long countByActiveTrue();

    long countByActiveFalse();

    @Query("""
        SELECT COUNT(u)
        FROM User u
        WHERE SIZE (u.wallets) = :walletCount
        """)
    long countUsersByWalletCount(@Param("walletCount")int walletCount);


}
