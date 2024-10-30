package com.app.toaster.auth.service.apple.verify;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.auth.service.apple.response.ApplePublicKey;
import com.app.toaster.auth.service.apple.response.ApplePublicKeys;

@Component
public class PublicKeyGenerator {

	private static final String SIGN_ALGORITHM_HEADER_KEY = "alg";
	private static final String KEY_ID_HEADER_KEY = "kid";
	private static final int POSITIVE_SIGN_NUMBER = 1;

	public PublicKey generatePublicKey(Map<String, String> headers, ApplePublicKeys applePublicKeys) {
		ApplePublicKey applePublicKey =
			applePublicKeys.getMatchesKey(headers.get(SIGN_ALGORITHM_HEADER_KEY), headers.get(KEY_ID_HEADER_KEY));
		return generatePublicKeyWithApplePublicKey(applePublicKey);
	}

	private PublicKey generatePublicKeyWithApplePublicKey(ApplePublicKey publicKey) {
		byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n());
		byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e());

		BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
		BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

		try {
			KeyFactory keyFactory = KeyFactory.getInstance(publicKey.kty());
			return keyFactory.generatePublic(publicKeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
			throw new CustomException(Error.CREATE_PUBLIC_KEY_EXCEPTION, Error.CREATE_PUBLIC_KEY_EXCEPTION.getMessage());
		}
	}
}
