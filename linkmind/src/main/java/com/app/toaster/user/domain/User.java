package com.app.toaster.user.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String socialId;

	@Column(nullable = true)
	private String refreshToken;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	@Column(nullable = true)
	private String fcmToken;

	@Column(nullable = true)
	private Boolean fcmIsAllowed = true;

	@Column(nullable = true)
	private String profile;

	@Builder
	public User(String nickname, String socialId, SocialType socialType) {
		this.nickname = nickname;
		this.socialId = socialId;
		this.socialType = socialType;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

	public void updateFcmIsAllowed(Boolean isAllowed){this.fcmIsAllowed = isAllowed;}

	public String getFcmToken() {
		if (Objects.nonNull(this.fcmToken)) {
			return this.fcmToken;
		}
		return null;
	}

	public void updateProfile(String profile){
		this.profile = profile;
	}

}
