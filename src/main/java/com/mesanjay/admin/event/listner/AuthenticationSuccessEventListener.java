package com.mesanjay.admin.event.listner;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private  final AdminRepository adminRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String userName = ((UserDetails) event.getAuthentication().
                getPrincipal()).getUsername();

        Admin admin = adminRepository.findByUsername(userName);
        admin.setLoginsCount(admin.getLoginsCount() == null ?  1 : admin.getLoginsCount() + 1);
            admin.setLastLogin(new Date());
        InetAddress IP;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        admin.setLastIp(IP.getHostAddress());
        adminRepository.save(admin);

    }
}