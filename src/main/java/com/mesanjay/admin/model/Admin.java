package com.mesanjay.admin.model;

import com.mesanjay.admin.utils.Strings;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admins", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "email"}))
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean emailVerified;

    private String username;

    private String password;

    private boolean isEnabled = false;

    private boolean isAccountNonLocked;

    private int failedAttempt;

    @JsonFormat(pattern = Strings.DATE_TIME_PATTERN, timezone = Strings.TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockTime;

    @JsonFormat(pattern = Strings.DATE_TIME_PATTERN, timezone = Strings.TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @JsonFormat(pattern = Strings.DATE_TIME_PATTERN, timezone = Strings.TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @JsonFormat(pattern = Strings.DATE_TIME_PATTERN, timezone = Strings.TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogout;

    private String lastIp;

    private Date lastPasswordReset;

    private Integer loginsCount;

    private boolean isBlocked = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "admins_roles", joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;
}
