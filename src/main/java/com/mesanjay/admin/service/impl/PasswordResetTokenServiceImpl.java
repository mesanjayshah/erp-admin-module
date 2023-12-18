package com.mesanjay.admin.service.impl;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.PasswordResetToken;
import com.mesanjay.admin.repository.AdminRepository;
import com.mesanjay.admin.repository.PasswordResetTokenRepository;
import com.mesanjay.admin.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AdminRepository adminRepository;

    @Override
    public String validatePasswordResetToken(String theToken) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(theToken);

        if (passwordResetToken == null ){
            return "invalid";
        }

        Calendar calendar = Calendar.getInstance();
        if ((passwordResetToken.getExpirationTime().getTime()-calendar.getTime().getTime())<= 0){
            return "expired";
        }
        return "valid";
    }

    @Override
    public Optional<Admin> findAdminByPasswordResetToken(String theToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(theToken).getAdmin());
    }

    @Override
    public void resetPassword(Admin admin, String newPassword) {
        log.info("we are saving resetting password");
        adminRepository.save(admin);
    }

    @Override
    public void createPasswordResetTokenForAdmin(Admin admin, String passwordResetToken) {
        var resetToken = new PasswordResetToken(passwordResetToken, admin);
        passwordResetTokenRepository.save(resetToken);
    }

}
