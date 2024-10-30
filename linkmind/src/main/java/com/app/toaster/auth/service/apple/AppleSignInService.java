package com.app.toaster.auth.service.apple;

import java.security.PublicKey;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.app.toaster.auth.service.apple.response.ApplePublicKeys;
import com.app.toaster.auth.service.apple.verify.AppleJwtParser;
import com.app.toaster.auth.service.apple.verify.PublicKeyGenerator;
import com.app.toaster.auth.service.kakao.LoginResult;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleSignInService {
	private static final String APPLE_URI = "https://appleid.apple.com/auth";
	private static final RestClient restClient = RestClient.create(APPLE_URI);
	private final AppleJwtParser appleJwtParser;
	private final PublicKeyGenerator publicKeyGenerator;

	public LoginResult getAppleId(String identityToken) {
		Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);

		ResponseEntity<ApplePublicKeys> result = restClient.get()
			.uri("/keys")
			.retrieve()
			.toEntity(ApplePublicKeys.class);
		PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, result.getBody());
		Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
		return LoginResult.of(claims.getSubject(),null,null);
	}
}
