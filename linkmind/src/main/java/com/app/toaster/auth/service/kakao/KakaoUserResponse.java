package com.app.toaster.auth.service.kakao;

public record KakaoUserResponse(KakaoAccount kakaoAccount) {
	public static KakaoUserResponse of(KakaoAccount kakaoAccount){
		return new KakaoUserResponse(kakaoAccount);
	}
}
