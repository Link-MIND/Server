package com.app.toaster.user.controller;

import java.util.HashMap;
import java.util.Map;

import com.app.toaster.controller.response.main.MainPageResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.config.UserId;
import com.app.toaster.user.controller.request.UpdateAllowedPush;
import com.app.toaster.exception.Success;
import com.app.toaster.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	@PatchMapping("/notification")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse updateAllowedNotification (@UserId Long userId, @RequestBody @Valid final UpdateAllowedPush requestDto) {
		Map<String, Boolean> result = new HashMap<String, Boolean>() {
			{
				put("isAllowed", userService.allowedPushNotification(userId, requestDto.allowedPush()));
			}
		};
		return ApiResponse.success(Success.UPDATE_PUSH_ALLOWED_SUCCESS, result);
	}

	@GetMapping("/my-page")
	public ApiResponse getMyPage(@UserId Long userId){
		return ApiResponse.success(Success.GET_MYPAGE_SUCCESS,userService.getMyPage(userId));
	}

	@GetMapping("/settings")
	public ApiResponse getSettings(@UserId Long userId){
		return ApiResponse.success(Success.GET_SETTINGS_SUCCESS,userService.getSettings(userId));
	}

	@GetMapping("/main")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<MainPageResponseDto> getTimer(
			@UserId Long userId) {

		return ApiResponse.success(Success.GET_USER_MAIN_SUCCESS,userService.getMainPage(userId) );
	}
}
