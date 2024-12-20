package com.app.toaster.admin.config;

import com.app.toaster.admin.entity.VerifiedAdmin;
import com.app.toaster.admin.entity.ToasterAdmin;
import com.app.toaster.admin.infrastructure.VerifiedAdminRepository;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class QrMfaAuthenticator {

    private String secret;
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    private final VerifiedAdminRepository verifiedAdminRepository;


    public QrMfaAuthenticator(@Value("${admin.secret}") final String secret, final VerifiedAdminRepository verifiedAdminRepository) {
        this.secret = secret;
        this.verifiedAdminRepository = verifiedAdminRepository;
    }


    public MultipartFile generateQrCode(String userKey) {
        String data = makeQrDataString(userKey);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 240, 240);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            ImageIO.write(image, "png", byteArrayOutputStream);
            byteArrayOutputStream.close();

            byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();

            return new MockMultipartFile("qrCode", "qrcode.png", "image/png", qrCodeBytes);
        } catch (WriterException | IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private String makeQrDataString(String userKey) {
        return "otpauth://totp/toaster?secret=" + userKey + "&issuer=Google";
    }

    public ToasterAdmin verifyGoogleTotpCode(Integer verificationCode, Long id) {

        VerifiedAdmin admin = verifiedAdminRepository.findById(id).orElseThrow(
                () -> new CustomException(Error.NOT_FOUND_USER_EXCEPTION, "어드민이 존재하지않는다.")
        );
        System.out.println(admin.getAdmin().getUsername());

        if (verificationCode != null) {
            try {
                if (!googleAuthenticator.authorize(admin.getOtpSecretKey(), verificationCode)) {
                    throw new CustomException(Error.BAD_REQUEST_VALIDATION, "유효하지 않은 인증코드입니다.");
                }
                admin.authorize();
                admin.verifiedAdmin();
                return admin.getAdmin();
            } catch (Exception e) {
                log.error("인증 쪽에서 에러 발생.");
            }
        } else {
            throw new CustomException(Error.TOKEN_TIME_EXPIRED_EXCEPTION, "만료된 코드 입니다.");
        }
        return null;
    }


}
