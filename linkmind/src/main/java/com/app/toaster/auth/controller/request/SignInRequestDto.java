package com.app.toaster.auth.controller.request;

public record SignInRequestDto(String socialType, String fcmToken) {
}
