package com.app.toaster.admin.service;

import com.app.toaster.admin.controller.dto.command.VerifyNewAdminCommand;
import com.app.toaster.admin.entity.VerifiedAdmin;
import com.app.toaster.admin.entity.ToasterAdmin;
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

import java.util.Optional;

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
    public VerifyNewAdminCommand registerVerifiedUser(final ToasterAdmin toasterAdmin, boolean isNewAdmin) {

        String otpKey = null;
        Long id = null;

        if (isNewAdmin) { //새로운 어드민의 경우 등록.

            GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

            VerifiedAdmin verifiedAdmin = VerifiedAdmin.builder()
                    .admin(toasterAdmin)
                    .build();

            otpKey = key.getKey();
            verifiedAdmin.changeOtpSecretKey(otpKey);

            id = verifiedAdminRepository.save(verifiedAdmin).getId();

        } else { //기존 경우의 경우는 그냥 찾기.

            VerifiedAdmin existVerifiedAdmin = verifiedAdminRepository.findByAdmin(toasterAdmin)
                    .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "찾을 수 없는 어드민 증명"));
            id = existVerifiedAdmin.getId();
            otpKey = existVerifiedAdmin.getOtpSecretKey();

        }

        return new VerifyNewAdminCommand(id, otpKey, isNewAdmin);
    }

    @Transactional
    public VerifyNewAdminCommand registerAdmin(String username, String password) {

        for (String adminString : adminList.split(salt)) {

            if (adminString.equals(username)) {

                ToasterAdmin existAdmin = findExistAdminPreVerification(username, password);

                if (existAdmin != null) {
                    if (existAdmin.verifyLastDate()) { //검증된 경우면 걍 어드민을 리턴.
                        return registerVerifiedUser(existAdmin, false);
                    }
                    return registerVerifiedUser(existAdmin, true);
                }


                String encPassword = passwordEncoder.encode(password);

                ToasterAdmin toasterAdmin = ToasterAdmin.builder()
                        .username(username)
                        .password(encPassword)
                        .build();

                return registerVerifiedUser(adminRepository.save(toasterAdmin), true);
            }
        }
        throw new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "어드민이 아닙니다.");
    }

    public ToasterAdmin findExistAdminPreVerification(String username, String password) {
        Optional<ToasterAdmin> admin = adminRepository.findByUsername(username);
        if (admin.isEmpty()){
            return null;
        }

        if (passwordEncoder.matches(password, admin.get().getPassword())) {
            return admin.get();
        }

        return null; //TODO: 다른 엣지 케이스가 더 있는지 생각해보고 없으면 걍 바로 에러 throw
    }

}
