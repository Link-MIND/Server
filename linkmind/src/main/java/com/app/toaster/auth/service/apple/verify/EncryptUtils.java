package com.app.toaster.auth.service.apple.verify;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;

public class EncryptUtils {

	public static String encrypt(String value) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] digest = sha256.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : digest) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new CustomException(Error.INVALID_ENCRYPT_COMMUNICATION, Error.INVALID_ENCRYPT_COMMUNICATION.getMessage());
		}
	}
}
