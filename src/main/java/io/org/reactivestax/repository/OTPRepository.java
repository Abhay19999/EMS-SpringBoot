package io.org.reactivestax.repository;

import io.org.reactivestax.domain.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findFirstByClientIdOrderByCreatedAtDesc(Long clientId);

}
