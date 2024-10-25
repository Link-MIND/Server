package com.app.toaster.auth.controller.response;

public record TokenHealthDto(boolean tokenHealth) {
	public static TokenHealthDto of(boolean tokenHealth){return new TokenHealthDto(tokenHealth);}
}
