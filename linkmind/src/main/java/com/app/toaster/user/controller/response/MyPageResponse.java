package com.app.toaster.user.controller.response;

public record MyPageResponse(String nickname, String profile, Long allReadToast, Long thisWeekendRead, Long thisWeekendSaved ) {
	public static MyPageResponse of(String nickname, String profile, Long allReadToast, Long thisWeekendRead, Long thisWeekendSaved ){
		return new MyPageResponse(nickname, profile, allReadToast, thisWeekendRead, thisWeekendSaved);
	}
}
