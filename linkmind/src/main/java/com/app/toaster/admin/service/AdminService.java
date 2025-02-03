package com.app.toaster.admin.service;

import com.app.toaster.admin.controller.dto.command.VerifyNewAdminCommand;
import com.app.toaster.admin.domain.AdminStatus;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
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
    public VerifyNewAdminCommand registerVerifiedUser(final ToasterAdmin toasterAdmin, AdminStatus adminStatus) {

        String otpKey = null;
        Long id = null;

        Optional<VerifiedAdmin> existVerifiedAdmin = verifiedAdminRepository.findByAdmin(toasterAdmin);

        if (AdminStatus.NEED_RENEW.equals(adminStatus) || AdminStatus.FIRST_REGISTER.equals(adminStatus)) { //새로운 어드민의 경우 등록.
            log.info("갱신해야되는 케이스.");

            deletePastVerify(existVerifiedAdmin); //새로운 어드민이면 여기서 기존 verify삭제 안함.

            GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

            VerifiedAdmin verifiedAdmin = VerifiedAdmin.builder()
                    .admin(toasterAdmin)
                    .build();


            otpKey = key.getKey();
            verifiedAdmin.changeOtpSecretKey(otpKey);

            id = verifiedAdminRepository.save(verifiedAdmin).getId();

        } else { //기존 경우의 경우는 그냥 찾기.
            log.info("기존의 경우로 넘어왔숨.");

            if (existVerifiedAdmin.isEmpty()){
                throw new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "찾을 수 없는 어드민 증명");
            }

            id = existVerifiedAdmin.get().getId();
            otpKey = existVerifiedAdmin.get().getOtpSecretKey();

        }

        return new VerifyNewAdminCommand(id, otpKey, adminStatus);
    }

    @Transactional
    public VerifyNewAdminCommand registerAdmin(String username, String password) {

        for (String adminString : adminList.split(salt)) {

            if (adminString.equals(username)) {

                ToasterAdmin existAdmin = findExistAdminPreVerification(username, password); //암호화 된 패스워드로 이미 했던적있는지 확인.

                if (existAdmin != null) {
                    log.info("존재합니다. 전 이 게임을 해봤어요.");
                    if (existAdmin.verifyLastDate()) { //검증된 경우면 걍 어드민을 리턴.
                        return registerVerifiedUser(existAdmin, AdminStatus.ACCEPTED);
                    }else{
                        return registerVerifiedUser(existAdmin, AdminStatus.NEED_RENEW); //아닌 경우는 갱신을 해야됨.
                    }
                }

                //id는 알고있음. Password를 통한 관리자 회원가입 시키기.
                log.info("디비에 어드민이 존재하지않아 어드민 회원가입 진행.");
                String encPassword = passwordEncoder.encode(password.toLowerCase());

                ToasterAdmin toasterAdmin = ToasterAdmin.builder()
                        .username(username)
                        .password(encPassword)
                        .build();

                return registerVerifiedUser(adminRepository.save(toasterAdmin), AdminStatus.FIRST_REGISTER);
            }
        }
        throw new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "어드민이 아닙니다.");
    }
    @Transactional
    public void deletePastVerify(Optional<VerifiedAdmin> existVerifiedAdmin){
        if(existVerifiedAdmin.isPresent()){
            verifiedAdminRepository.delete(existVerifiedAdmin.get());
        }
    }

    public ToasterAdmin findExistAdminPreVerification(String username, String password) {
        Optional<ToasterAdmin> admin = adminRepository.findByUsername(username);
        log.info("admin이 이미 존재하는지 password match 진행.");
        if (admin.isEmpty()){
            return null;
        }

        if (passwordEncoder.matches(password.toLowerCase(), admin.get().getPassword())) {
            return admin.get();
        }else{
            throw new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "비밀번호가 틀립니다.");
        }

    }

}
