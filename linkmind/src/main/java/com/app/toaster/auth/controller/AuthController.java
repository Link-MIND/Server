package com.app.toaster.auth.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.toaster.common.dto.ApiResponse;
// import com.app.toaster.config.UserId;
import com.app.toaster.config.UserId;
import com.app.toaster.auth.controller.request.SignInRequestDto;
import com.app.toaster.auth.controller.response.SignInResponseDto;
import com.app.toaster.auth.controller.response.TokenHealthDto;
import com.app.toaster.auth.controller.response.TokenResponseDto;
import com.app.toaster.exception.Success;
import com.app.toaster.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<SignInResponseDto> signIn(
		@RequestHeader("Authorization") String socialAccessToken,
		@RequestBody SignInRequestDto requestDto
	) throws IOException {
		return ApiResponse.success(Success.LOGIN_SUCCESS, authService.signIn(socialAccessToken, requestDto));
	}

	@PostMapping("/token")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<TokenResponseDto> reissueToken(@RequestHeader String refreshToken) {
		return ApiResponse.success(Success.RE_ISSUE_TOKEN_SUCCESS, authService.issueToken(refreshToken));
	}

	@PostMapping("/sign-out")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse signOut(@UserId Long userId) {
		authService.signOut(userId);
		return ApiResponse.success(Success.SIGNOUT_SUCCESS);
	}

	@DeleteMapping("/withdraw")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse withdraw(@UserId Long userId) {
		authService.withdraw(userId);
		return ApiResponse.success(Success.DELETE_USER_SUCCESS);
	}

	@PostMapping("/token/health")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<TokenHealthDto> checkHealthOfToken(@RequestHeader String token) {
		return ApiResponse.success(Success.TOKEN_HEALTH_CHECKED_SUCCESS, authService.checkHealthOfToken(token));
	}
}
