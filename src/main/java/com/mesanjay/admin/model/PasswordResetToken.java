package com.mesanjay.admin.model;

import com.mesanjay.admin.utils.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PasswordResetToken {

    @SequenceGenerator(
            name = "password_reset_token_sequence",
            sequenceName = "password_reset_token_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "password_reset_token_sequence"
    )
    private Long id;

    private String token;
    private Date expirationTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public PasswordResetToken(String token, Admin admin) {
        super();
        this.token = token;
        this.admin = admin;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }

    public PasswordResetToken(String token) {
        super();
        this.token = token;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }

}
