package com.app.toaster.auth.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.app.toaster.common.config.jwt.JwtService;
import com.app.toaster.auth.controller.request.SignInRequestDto;
import com.app.toaster.auth.controller.response.SignInResponseDto;
import com.app.toaster.auth.controller.response.TokenHealthDto;
import com.app.toaster.auth.controller.response.TokenResponseDto;
import com.app.toaster.user.domain.SocialType;
import com.app.toaster.user.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.exception.model.UnprocessableEntityException;
import com.app.toaster.external.client.discord.DiscordMessageProvider;
import com.app.toaster.external.client.discord.NotificationDto;
import com.app.toaster.external.client.discord.NotificationType;
import com.app.toaster.external.client.slack.SlackApi;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.timer.infrastructure.TimerRepository;
import com.app.toaster.toast.infrastructure.ToastRepository;
import com.app.toaster.user.infrastructure.UserRepository;
import com.app.toaster.popup.infrastructure.PopupManagerRepository;
import com.app.toaster.auth.service.apple.AppleSignInService;
import com.app.toaster.auth.service.kakao.KakaoSignInService;
import com.app.toaster.auth.service.kakao.LoginResult;
import com.app.toaster.toast.service.ToastService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final AppleSignInService appleSignInService;
	private final KakaoSignInService kakaoSignInService;
	private final JwtService jwtService;
	private final ToastService toastService;

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final PopupManagerRepository popupManagerRepository;

	private final SlackApi slackApi;
	private final DiscordMessageProvider discordMessageProvider;


	private final Long TOKEN_EXPIRATION_TIME_ACCESS =  7*24*60 * 60 * 1000L; //7일
	private final Long TOKEN_EXPIRATION_TIME_REFRESH = 14*24*60 * 60 * 1000L; //14일
	@Value("${static-image.root}")
	private String BASIC_ROOT;

	@Value("${static-image.url}")
	private String BASIC_THUMBNAIL;
	private final ToastRepository toastRepository;
	private final TimerRepository timerRepository;

	@Transactional
	public SignInResponseDto signIn(String socialAccessToken, SignInRequestDto requestDto) throws IOException {
		SocialType socialType = SocialType.valueOf(requestDto.socialType());
		LoginResult loginResult = login(socialType, socialAccessToken);
		String socialId = loginResult.id();
		String profileImage = loginResult.profile();
		String nickname = loginResult.nickname();
		Boolean isRegistered = userRepository.existsBySocialIdAndSocialType(socialId, socialType);

		if (!isRegistered) {
			User newUser = User.builder()
				.nickname(nickname==null?"토스터":nickname)
				.socialId(socialId)
				.socialType(socialType).build();
			newUser.updateFcmIsAllowed(true); //신규 유저면 true박고
			userRepository.save(newUser);

			discordMessageProvider.sendNotification(new NotificationDto(NotificationType.SIGNUP,null,null));
		}

		User user = userRepository.findBySocialIdAndSocialType(socialId, socialType)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));


		// jwt 발급 (액세스 토큰, 리프레쉬 토큰)
		String accessToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_ACCESS);
		String refreshToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_REFRESH);
		String fcmToken = requestDto.fcmToken();

		user.updateRefreshToken(refreshToken);
		user.updateFcmToken(fcmToken);
		// user.updateProfile(profileImage == null ? BASIC_ROOT+BASIC_THUMBNAIL : profileImage);
		user.updateProfile(BASIC_ROOT+BASIC_THUMBNAIL);

		if (nickname!=null){		//탈퇴 안했던 유저들도 수정될 수 있도록 변경
			user.updateNickname(nickname);
		}
		return SignInResponseDto.of(user.getUserId(), accessToken, refreshToken, fcmToken, isRegistered,user.getFcmIsAllowed(),
			user.getProfile());
	}

	@Transactional
	public TokenResponseDto issueToken(String refreshToken) {
		jwtService.verifyToken(refreshToken);

		User user = userRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

		// jwt 발급 (액세스 토큰, 리프레쉬 토큰)
		String newAccessToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_ACCESS);
		String newRefreshToken = jwtService.issuedToken(String.valueOf(user.getUserId()), TOKEN_EXPIRATION_TIME_REFRESH);

		user.updateRefreshToken(newRefreshToken);

		return TokenResponseDto.of(newAccessToken, newRefreshToken);
	}

	@Transactional
	public void signOut(Long userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
		user.updateRefreshToken(null);
		user.updateFcmToken(null);
	}

	private LoginResult login(SocialType socialType, String socialAccessToken) {
		if (socialType.toString() == "APPLE") {
			return appleSignInService.getAppleId(socialAccessToken);
		}
		else if (socialType.toString() == "KAKAO") {

			return kakaoSignInService.getKaKaoId(socialAccessToken);
		}
		else{
			return LoginResult.of("test", "뭔가 로직에 문제가 있음.","닉네임에 문제가 있음.");
		}
	}

	@Transactional
	public void withdraw(Long userId) {
		User user = userRepository.findByUserId(userId).orElseThrow(
			()->new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));
		if (user.getSocialType() == SocialType.KAKAO){
			String deleteSocialId = kakaoSignInService.withdrawKakao(user.getSocialId());
			System.out.println(deleteSocialId);
		}
		try {
			toastService.deleteAllToast(user);
		}catch (IOException e){
			throw new CustomException(Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION, Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION.getMessage());
		}
		timerRepository.deleteAllByUser(user);
		categoryRepository.deleteAllByUser(user);
		popupManagerRepository.deleteAllByUserId(userId);

		Long res = userRepository.deleteByUserId(userId); //res가 삭제된 컬럼의 개수 즉, 1이 아니면 뭔가 알 수 없는 에러.

		System.out.println(res + "개의 컬럼이 삭제되었습니다.");
		if (res!=1){
			throw new UnprocessableEntityException(Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION, Error.UNPROCESSABLE_ENTITY_DELETE_EXCEPTION.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public TokenHealthDto checkHealthOfToken(String refreshToken){
		return TokenHealthDto.of(jwtService.verifyToken(refreshToken));
	}


}
