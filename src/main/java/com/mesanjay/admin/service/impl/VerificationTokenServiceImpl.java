package com.mesanjay.admin.service.impl;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.VerificationToken;
import com.mesanjay.admin.repository.AdminRepository;
import com.mesanjay.admin.repository.VerificationTokenRepository;
import com.mesanjay.admin.service.VerificationTokenService;
import com.mesanjay.admin.utils.TokenExpirationTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepository tokenRepository, AdminRepository adminRepository) {
        this.tokenRepository = tokenRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public String validateToken(String token) {

        Optional<VerificationToken> tokenOptional = tokenRepository.findByToken(token);

        if(tokenOptional.isEmpty()){
            log.info("invalid token");
            return "Invalid";
        }

        Admin admin  = tokenOptional.get().getAdmin();
        Calendar calendar = Calendar.getInstance();
        Date expiredAt = tokenOptional.get().getExpirationTime();

        if(expiredAt.before(new Date())) {
            log.info("expired token");
            return "Expired";
        }

/*        if(tokenOptional.get().getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            log.info("expired token");
            return "Expired";
        }*/

        admin.setEnabled(true);
        admin.setEmailVerified(true);
        adminRepository.save(admin);
        log.info("valid token");
        return "Valid";
    }

    @Override
    public void saveVerificationTokenForAdmin(Admin admin, String token) {
        log.info("save verification token for admin account email verification {}", token);
        var verificationToken = new VerificationToken(token, admin);
        verificationToken.setExpirationTime(TokenExpirationTime.getExpirationTime());
        System.out.println(verificationToken.getToken() + verificationToken.getExpirationTime() + "  test");
        tokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteAdminToken(Long id) {
        tokenRepository.deleteByAdminId(id);
    }

}
