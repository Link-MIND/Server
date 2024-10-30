package com.app.toaster.popup.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.toaster.popup.controller.request.PopUpRequestDto;
import com.app.toaster.popup.controller.response.InvisibleResponseDto;
import com.app.toaster.popup.controller.response.PopupResponseDto;
import com.app.toaster.popup.service.PopupService;
import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.common.config.UserId;
import com.app.toaster.exception.Success;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/popup")
@Validated
public class PopupController {
	private final PopupService popupService;


	@PatchMapping
	public ApiResponse<InvisibleResponseDto> updateInvisible(@UserId Long userId, @RequestBody @Valid PopUpRequestDto popUpRequestDto){
		return ApiResponse.success(Success.UPDATE_POPUP_SUCCESS, popupService.updatePopupInvisible(userId,popUpRequestDto));
	}

	@GetMapping
	public ApiResponse<PopupResponseDto> getPopUpInformation(@UserId Long userId){
		return ApiResponse.success(Success.GET_POPUP_SUCCESS, popupService.findPopupInformation(userId));
	}

}
