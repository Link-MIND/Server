package com.app.toaster.admin.service;

import com.app.toaster.admin.domain.VerifiedAdmin;
import com.app.toaster.admin.entity.Admin;
import com.app.toaster.admin.infrastructure.AdminRepository;
import com.app.toaster.admin.infrastructure.VerifiedAdminRepository;
import com.app.toaster.auth.controller.response.TokenResponseDto;
import com.app.toaster.common.config.jwt.JwtService;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.user.domain.User;
import com.app.toaster.user.infrastructure.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final VerifiedAdminRepository verifiedAdminRepository;
    private final AdminRepository adminRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @Value(value = "${admin.adminList}")
    private String adminList;

    @Value(value = "${admin.salt}")
    private String salt;



    @Transactional
    public TokenResponseDto issueToken(Long testUserId) {

        User user = userRepository.findById(testUserId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        // jwt 발급 (액세스 토큰, 리프레쉬 토큰)
        String newAccessToken = jwtService.issuedToken(String.valueOf(user.getUserId()), 100000000000L);
        String newRefreshToken = jwtService.issuedToken(String.valueOf(user.getUserId()), 10000000000L);

        user.updateRefreshToken(newRefreshToken);

        return TokenResponseDto.of(newAccessToken, newRefreshToken);
    }


    @Transactional
    public String registerVerifiedUser(final Admin admin) {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

        VerifiedAdmin verifiedAdmin = VerifiedAdmin.builder()
                .admin(admin)
                .build();

        verifiedAdmin.changeOtpSecretKey(key.getKey());
        Long id = verifiedAdminRepository.save(verifiedAdmin).getId();
        return key.getKey()+"id="+id;
    }

    @Transactional
    public Admin registerAdmin(String username, String password) {

        for (String adminString : adminList.split(salt)) {

            if (adminString.equals(username)) {

                String encPassword = passwordEncoder.encode(password);

                Admin admin = Admin.builder()
                        .username(username)
                        .password(encPassword)
                        .build();

                return adminRepository.save(admin);
            }
        }

        throw new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "어드민이 아닙니다.");
    }

}
