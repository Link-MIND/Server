package com.app.toaster.search.controller.response;

public record ToastResult(Long toastId, String title) {
	public static ToastResult of(Long toastId, String title){
		return new ToastResult(toastId, title);
	}
}
