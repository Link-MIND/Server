package com.app.toaster.admin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToasterAdmin {

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

    @Column(name = "lastVerifiedDate")
    private LocalDate lastTestDate;

    @Builder
    public ToasterAdmin(String username, String password){
        this.username = username;
        this.password = password;
        this.verified = false;
        this.lastTestDate = LocalDate.now();
    }

    public VerifiedAdmin authorize(){
        return VerifiedAdmin.builder()
                .admin(this)
                .build();

    }

    public void verify(){
        this.lastTestDate = LocalDate.now();
        this.verified = true;
    }

    public boolean verifyLastDate(){
        return LocalDate.now().isBefore(this.lastTestDate.plusDays(1));
    }

}
