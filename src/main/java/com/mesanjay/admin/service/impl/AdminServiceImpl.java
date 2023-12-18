package com.mesanjay.admin.service.impl;

import com.mesanjay.admin.dto.AdminDto;
import com.mesanjay.admin.exception.InvalidVerificationTokenException;
import com.mesanjay.admin.exception.UserAlreadyExistsException;
import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.Role;
import com.mesanjay.admin.model.VerificationToken;
import com.mesanjay.admin.repository.AdminRepository;
import com.mesanjay.admin.repository.RoleRepository;
import com.mesanjay.admin.repository.VerificationTokenRepository;
import com.mesanjay.admin.service.AdminService;
import com.mesanjay.admin.utils.MCBConstant;
import com.mesanjay.admin.utils.TokenExpirationTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    public static final int MAX_FAILED_ATTEMPTS = 3;  //limit attempt
    public static final long LOCK_TIME_DURATION = 300000;  //5 min

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;


    @Override
    public Admin save(AdminDto adminDto) throws UnknownHostException {
        
        Admin adminExists = this.findByUsername(adminDto.getUsername());

        if(adminExists != null){
            throw new UserAlreadyExistsException("User with email " + adminDto.getUsername() + " already exists");
        }

        Role role = roleRepository.findByName(MCBConstant.Roles.ADMIN);
        if (role == null) {
            role = roleRepository.save(new Role(MCBConstant.Roles.ADMIN));
        }

        Admin admin = getAdmin(adminDto, role);
        return adminRepository.save(admin);
    }

    private static Admin getAdmin(AdminDto adminDto, Role role) throws UnknownHostException {
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setUsername(adminDto.getUsername());
        admin.setEmail(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setCreatedAt(new Date());

        InetAddress IP=InetAddress.getLocalHost();
        admin.setLastIp(IP.getHostAddress());
        admin.setEnabled(false);
        admin.setAccountNonLocked(true);
        admin.setRoles(List.of(role));
        return admin;
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public void increaseFailedAttempt(Admin admin) {
        int attempt = admin.getFailedAttempt() + 1;
        adminRepository.updateFailedAttempt(attempt, admin.getUsername());
    }

    @Override
    public void resetAttempt(String email) {
        adminRepository.updateFailedAttempt(0, email);
    }

    @Override
    public void lock(Admin admin) {
        admin.setAccountNonLocked(false);
        admin.setLockTime(new Date());
        adminRepository.save(admin);
    }

    @Override
    public boolean unlockAccountTimeExpired(Admin admin) {

        Date lockTime = admin.getLockTime();
        if (lockTime != null) {
            long lockTimeInMills = lockTime.toInstant().toEpochMilli();
            long currentTimeMillis = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            long unlockTimeMillis = lockTimeInMills + LOCK_TIME_DURATION;

            System.out.println(currentTimeMillis + " > " + unlockTimeMillis);
            if (currentTimeMillis > unlockTimeMillis) {
                admin.setAccountNonLocked(true);
                admin.setLockTime(null);
                admin.setFailedAttempt(0);
                adminRepository.save(admin);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Admin> getExpiredLockedAdmins() {
        return adminRepository.findExpiredLockedAdmin(new Date());
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {

        VerificationToken verificationToken = tokenRepository.findByToken(oldToken).orElseThrow(() ->
                new InvalidVerificationTokenException("test"));

        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpirationTime(TokenExpirationTime.getExpirationTime());

        return tokenRepository.save(verificationToken);
    }

}
