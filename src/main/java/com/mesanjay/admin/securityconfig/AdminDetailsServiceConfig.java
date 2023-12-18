package com.mesanjay.admin.securityconfig;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdminDetailsServiceConfig implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.isEnabled()) {
            return new User(
                    admin.getUsername(),
                    admin.getPassword(),
                    admin.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));

        } else
           throw new UsernameNotFoundException("Could not find username");

    }
}
