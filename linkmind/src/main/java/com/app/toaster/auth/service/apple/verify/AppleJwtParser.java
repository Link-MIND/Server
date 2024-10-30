package com.app.toaster.auth.service.apple.verify;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppleJwtParser {

	private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
	private static final int HEADER_INDEX = 0;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public Map<String, String> parseHeaders(String identityToken) {
		try {
			String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
			String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader));
			return OBJECT_MAPPER.readValue(decodedHeader, Map.class);

		} catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
			throw new CustomException(Error.INVALID_APPLE_IDENTITY_TOKEN, Error.INVALID_APPLE_IDENTITY_TOKEN.getMessage());
		}
	}

	public Claims parsePublicKeyAndGetClaims(String idToken, PublicKey publicKey) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(idToken)
				.getBody();

		} catch (ExpiredJwtException e) {
			throw new CustomException(Error.EXPIRED_APPLE_IDENTITY_TOKEN, Error.EXPIRED_APPLE_IDENTITY_TOKEN.getMessage());
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
			throw new CustomException(Error.INVALID_APPLE_IDENTITY_TOKEN, Error.INVALID_APPLE_IDENTITY_TOKEN.getMessage());
		}
	}
}