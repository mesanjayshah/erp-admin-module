package com.mesanjay.admin.securityconfig.custom;

import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.model.Role;
import com.mesanjay.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Admin admin = adminRepository.findByUsername(email);
        log.info("BEFORE admin is enabled or not {}", admin.isEnabled());
        if (admin != null && admin.isEnabled()) {
            log.info("admin is enabled or not {}", admin.isEnabled());
      /*      return new org.springframework.security.core.userdetails.User(admin.getUsername(),
                    admin.getPassword(),
                    mapRolesToAuthorities(admin.getRoles()));*/
            return new CustomUserDetails();
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return mapRoles;
    }

}
