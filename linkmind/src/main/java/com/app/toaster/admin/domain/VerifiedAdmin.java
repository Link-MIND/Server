package com.app.toaster.admin.domain;

import com.app.toaster.admin.entity.Admin;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor
public class VerifiedAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verified_admin_id")
    private Long id;

    @Column
    private String otpSecretKey; // OTP 비밀키

    @Column
    private boolean authorized;

    @OneToOne(optional = false)
    @JoinColumn(name="admin_id", unique=true, nullable=false, updatable=false)
    private Admin admin;

    @Builder
    public VerifiedAdmin(final Admin admin) {
        this.admin = admin;
        this.authorized = false;
    }

    public void changeOtpSecretKey(String otpSecretKey) {

        if (Objects.isNull(otpSecretKey) || otpSecretKey.isEmpty()) {
            throw new CustomException(Error.BAD_REQUEST_VALIDATION, "OTP 비밀키는 필수입력값입니다.");
        }

        this.otpSecretKey = otpSecretKey;
    }

    public void authorize(){
        this.authorized = true;
    }

    //
    public void verifiedAdmin(){
        if (!this.authorized){
            throw new CustomException(Error.UNAUTHORIZED_ACCESS, "권한이 없습니다.");
        }
        this.admin.verify();
    }



}
