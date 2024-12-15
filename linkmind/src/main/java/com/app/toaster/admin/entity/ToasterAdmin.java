package com.app.toaster.admin.entity;

import com.app.toaster.admin.domain.VerifiedAdmin;
import com.app.toaster.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "masterToken")
    private String masterToken;

    @Builder
    public Admin(String username, String password){
        this.username = username;
        this.password = password;
        this.verified = false;
    }

    public VerifiedAdmin authorize(){
        return VerifiedAdmin.builder()
                .admin(this)
                .build();

    }

    public void verify(){
        this.verified = true;
    }

}
