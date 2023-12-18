package com.mesanjay.admin.repository;

import com.mesanjay.admin.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String passwordResetToken);
}
