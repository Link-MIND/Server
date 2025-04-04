package com.app.toaster.category.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import com.app.toaster.auth.controller.AuthController;
import com.app.toaster.auth.service.AuthService;
import com.app.toaster.auth.service.kakao.KakaoSignInService;
import com.app.toaster.category.controller.request.ChangeCategoryPriorityDto;
import com.app.toaster.category.domain.Category;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.user.domain.SocialType;
import com.app.toaster.user.domain.User;
import com.app.toaster.user.infrastructure.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.toaster.user.domain.User;
import com.app.toaster.user.domain.SocialType;
import com.app.toaster.user.infrastructure.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles(profiles = "local")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private User user;
	private Category targetCategory;

	@BeforeEach
	void setUp() {
		user = userRepository.findByUserId(7L).orElseThrow();

		Category category1 = new Category("category1", user, 1);
		Category category2 = new Category("category2", user, 2);
		Category category3 = new Category("category3", user, 3);
		Category category4 = new Category("category4", user, 4);
		Category category5 = new Category("category5", user, 5);

		categoryRepository.saveAll(List.of(category1, category2, category3, category4, category5));
		targetCategory = category3;
	}

	@Test
	@DisplayName("우선순위를 증가하여 수정힌다.")
	void increaseCategoryPriority() {
		// Given

		// When
		categoryService.editCategoryPriority(new ChangeCategoryPriorityDto(targetCategory.getCategoryId(), 5));

		// Then
		List<Category> categoryList = categoryRepository.findAllByUserOrderByPriority(user);
		assertThat(categoryList.get(0).getTitle()).isEqualTo("category1");
		assertThat(categoryList.get(1).getTitle()).isEqualTo("category2");
		assertThat(categoryList.get(2).getTitle()).isEqualTo("category4");
		assertThat(categoryList.get(3).getTitle()).isEqualTo("category5");
		assertThat(categoryList.get(4).getTitle()).isEqualTo("category3");
	}

	@Test
	@DisplayName("우선순위를 감소하여 수정힌다.")
	void decreaseCategoryPriority() {
		// Given

		// When
		categoryService.editCategoryPriority(new ChangeCategoryPriorityDto(targetCategory.getCategoryId(), 1));

		// Then
		List<Category> categoryList = categoryRepository.findAllByUserOrderByPriority(user);
		assertThat(categoryList.get(0).getTitle()).isEqualTo("category3");
		assertThat(categoryList.get(1).getTitle()).isEqualTo("category1");
		assertThat(categoryList.get(2).getTitle()).isEqualTo("category2");
		assertThat(categoryList.get(3).getTitle()).isEqualTo("category4");
		assertThat(categoryList.get(4).getTitle()).isEqualTo("category5");
	}

	@Test
	@DisplayName("SELECT FOR UPDATE가 동시에 접근할 경우 대기하거나 충돌하는지 확인한다.")
	void testSelectForUpdateConcurrency() throws ExecutionException, InterruptedException {
		// 트랜잭션 템플릿 설정
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		CompletableFuture<Void> transaction1 = CompletableFuture.runAsync(() -> {
			transactionTemplate.execute(status -> {
				categoryService.editCategoryPriority(new ChangeCategoryPriorityDto(targetCategory.getCategoryId(), 5));
				try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			});
		});

		CompletableFuture<Void> transaction2 = CompletableFuture.runAsync(() -> {
			transactionTemplate.execute(status -> {
				categoryService.editCategoryPriority(new ChangeCategoryPriorityDto(targetCategory.getCategoryId(), 1));
				return null;
			});
		});

		CompletableFuture.allOf(transaction1, transaction2).join();

		// 결과 확인
		List<Category> categoryList = categoryRepository.findAllByUserOrderByPriority(user);
		assertThat(categoryList.get(0).getTitle()).isEqualTo("category3");
		assertThat(categoryList.get(1).getTitle()).isEqualTo("category1");
		assertThat(categoryList.get(2).getTitle()).isEqualTo("category2");
		assertThat(categoryList.get(3).getTitle()).isEqualTo("category4");
		assertThat(categoryList.get(4).getTitle()).isEqualTo("category3");
	}

	@AfterEach
	void finish(){
		categoryRepository.deleteAll();
	}


}

