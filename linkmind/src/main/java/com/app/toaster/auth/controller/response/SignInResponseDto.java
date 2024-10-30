package com.app.toaster.auth.controller.response;

public record SignInResponseDto(Long userId, String accessToken, String refreshToken, String fcmToken, Boolean isRegistered,Boolean fcmIsAllowed, String profile) {
	public static SignInResponseDto of(Long userId, String accessToken, String refreshToken, String fcmToken,
		Boolean isRegistered, Boolean fcmIsAllowed, String profile){
		return new SignInResponseDto(userId,accessToken, refreshToken,fcmToken,isRegistered,fcmIsAllowed,profile);
	}
}
