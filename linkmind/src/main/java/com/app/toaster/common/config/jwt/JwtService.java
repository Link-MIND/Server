package com.app.toaster.common.config.jwt;


import static io.jsonwebtoken.Jwts.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.exception.model.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@PostConstruct
	protected void init() {
		jwtSecret = Base64.getEncoder()
			.encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	// JWT 토큰 발급
	public String issuedToken(String userId, Long tokenExpirationTime) {
		final Date now = new Date();

		// 클레임 생성
		final Claims claims = claims()
			.setSubject("token")
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenExpirationTime));

		//private claim 등록
		claims.put("userId", userId);

		return builder()
			.setHeaderParam(Header.TYPE , Header.JWT_TYPE)
			.setClaims(claims)
			.signWith(getSigningKey())
			.compact();
	}

	private SecretKey getSigningKey() {
		final byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// JWT 토큰 검증
	public boolean verifyToken(String token) {
		try {
			final Claims claims = getBody(token);
			return true;
		} catch (RuntimeException e) {
			if (e instanceof ExpiredJwtException) {
				throw new UnauthorizedException(Error.TOKEN_TIME_EXPIRED_EXCEPTION, Error.TOKEN_TIME_EXPIRED_EXCEPTION.getMessage());
			}
			throw new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage());
		}
	}

	private Claims getBody(final String token) {
		return parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// JWT 토큰 내용 확인
	public String getJwtContents(String token) {
		final Claims claims = getBody(token);
		return (String) claims.get("userId");
	}
}
