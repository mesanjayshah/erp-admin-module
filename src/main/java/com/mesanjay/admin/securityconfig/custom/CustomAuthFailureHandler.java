package com.mesanjay.admin.securityconfig.custom;


import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.repository.AdminRepository;
import com.mesanjay.admin.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    public static final long ATTEMPT_TIME = 3;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        Admin admin = adminRepository.findByUsername(email);

        if (admin != null) {
            if (admin.isEnabled()) {
                if (admin.isAccountNonLocked()) {
                    if (admin.getFailedAttempt() < ATTEMPT_TIME - 1) {
                        adminService.increaseFailedAttempt(admin);
                    } else {
                        adminService.lock(admin);
                        exception = new LockedException("Your account is locked !! failed attempt 3");
                    }
                } else {
                    if (adminService.unlockAccountTimeExpired(admin)) {
                        exception = new LockedException("Account is unlocked! Please try to login");
                    } else {
                        exception = new LockedException("Account is locked! Please try after 24hours");
                    }
                }
            } else {
                exception = new LockedException("Account is inactive..verify account");
            }
        }
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}