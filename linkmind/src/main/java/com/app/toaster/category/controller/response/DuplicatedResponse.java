package com.app.toaster.category.controller.response;

public record DuplicatedResponse(Boolean isDupicated) {
	public static DuplicatedResponse of(Boolean isDupicated){
		return new DuplicatedResponse(isDupicated);
	}
}
