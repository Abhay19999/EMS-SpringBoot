package io.org.reactivestax.repository;

import io.org.reactivestax.domain.OTPLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPLoginRepository extends JpaRepository<OTPLogin,Long> {
    Optional<OTPLogin> findByUserId(String userId);
    Optional<OTPLogin> findFirstByUserIdOrderByCreatedAtDesc(String userId);
}
