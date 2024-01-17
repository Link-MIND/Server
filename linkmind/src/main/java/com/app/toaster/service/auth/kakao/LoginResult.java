package com.app.toaster.service.auth.kakao;

public record LoginResult(String id, String profile, String nickname) {
	public static LoginResult of(String id, String profile,String nickname){
		return new LoginResult(id,profile,nickname);
	}
}
