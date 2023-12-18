package com.mesanjay.admin.service;

import com.mesanjay.admin.dto.AdminDto;
import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.VerificationToken;

import java.net.UnknownHostException;
import java.util.List;

public interface AdminService {

    Admin save(AdminDto adminDto) throws UnknownHostException;

    Admin findByUsername(String username);

    void increaseFailedAttempt(Admin admin);

    void resetAttempt(String email);

    void lock(Admin admin);

    boolean unlockAccountTimeExpired(Admin admin);

    List<Admin> getExpiredLockedAdmins();

    VerificationToken generateNewVerificationToken(String oldToken);

}
