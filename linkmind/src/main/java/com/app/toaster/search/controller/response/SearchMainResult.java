package com.app.toaster.search.controller.response;

import java.util.List;

import com.app.toaster.toast.controller.response.ToastDto;

public record SearchMainResult(String keyword, List<ToastDto> toasts, List<CategoryResult> categories) {
	public static SearchMainResult of(String keyword, List<ToastDto> toasts, List<CategoryResult> categories){
		return new SearchMainResult(keyword,toasts,categories);
	}
}
