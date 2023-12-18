package com.mesanjay.admin.securityconfig.custom;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.repository.AdminRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final AdminRepository adminRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String userName = ((UserDetails) authentication.getPrincipal()).getUsername();

        Admin admin = adminRepository.findByUsername(userName);
             admin.setLastLogout(new Date());
        System.out.println(admin.getLastLogout() + " recent logout Date here");
        adminRepository.save(admin);

        super.onLogoutSuccess(request, response, authentication);
    }

}