package com.mesanjay.admin.securityconfig.custom;

import com.mesanjay.admin.model.Admin;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class CustomUserDetails implements UserDetails {

    private Admin admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(admin.getRoles()
                .toString().split(",")).map(
                SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        System.out.println("value of isEnabled ->>> " + admin.isEnabled());
        return admin.isEnabled();
    }
}
