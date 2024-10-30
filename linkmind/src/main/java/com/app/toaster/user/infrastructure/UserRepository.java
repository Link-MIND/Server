package com.app.toaster.user.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.toaster.user.domain.SocialType;
import com.app.toaster.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsBySocialIdAndSocialType(String socialId, SocialType socialType);

	Optional<User> findByUserId(Long userId);

	Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);

	Boolean existsByNickname(String s);

	Optional<User> findByRefreshToken(String refreshToken);

	Long deleteByUserId(Long userId);
}
