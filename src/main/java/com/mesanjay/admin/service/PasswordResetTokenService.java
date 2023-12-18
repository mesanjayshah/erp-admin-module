package com.mesanjay.admin.service;


import com.mesanjay.admin.model.Admin;

import java.util.Optional;

public interface PasswordResetTokenService {
    String validatePasswordResetToken(String theToken);

    Optional<Admin> findAdminByPasswordResetToken(String theToken);

    void resetPassword(Admin admin, String password);

    void createPasswordResetTokenForAdmin(Admin admin, String passwordResetToken);
}
