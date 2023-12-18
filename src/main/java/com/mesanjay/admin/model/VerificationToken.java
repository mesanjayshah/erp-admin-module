package com.mesanjay.admin.model;

import com.mesanjay.admin.utils.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VerificationToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public VerificationToken(String token, Admin admin) {
        this.token = token;
        this.admin = admin;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }

}
