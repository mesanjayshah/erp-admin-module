package com.mesanjay.admin.service;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {

    String validateToken(String token);
    void saveVerificationTokenForAdmin(Admin admin, String token);
    Optional<VerificationToken> findByToken(String token);

    void deleteAdminToken(Long id);

}
