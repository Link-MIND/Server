package com.app.toaster.auth.service.apple.response;

import java.util.List;

import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;

public record ApplePublicKeys(List<ApplePublicKey> keys) {

	public ApplePublicKey getMatchesKey(String alg, String kid) {
		return this.keys
			.stream()
			.filter(k -> k.alg().equals(alg) && k.kid().equals(kid))
			.findFirst()
			.orElseThrow(() -> new CustomException(Error.INVALID_APPLE_PUBLIC_KEY, Error.INVALID_APPLE_PUBLIC_KEY.getMessage()));
	}
}
