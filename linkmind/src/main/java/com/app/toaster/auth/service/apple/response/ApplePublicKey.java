package com.app.toaster.auth.service.apple.response;

public record ApplePublicKey(
	String kty,
	String kid,
	String use,
	String alg,
	String n,
	String e
) {
}
