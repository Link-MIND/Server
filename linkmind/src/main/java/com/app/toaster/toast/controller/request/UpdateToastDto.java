package com.app.toaster.toast.controller.request;

import com.app.toaster.utils.valid.Severity;
import com.app.toaster.utils.valid.TitleValid;

import com.app.toaster.utils.valid.marker.ToastValidationGroup;
import jakarta.validation.constraints.NotNull;

public record UpdateToastDto(Long toastId, @TitleValid(payload = Severity.Error.class, groups = {ToastValidationGroup.class}) @NotNull String title) {
	public static UpdateToastDto of(Long toastId, String title){
		return new UpdateToastDto(toastId,title);
	}
}
