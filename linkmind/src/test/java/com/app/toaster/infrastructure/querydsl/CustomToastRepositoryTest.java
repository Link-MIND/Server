// package com.app.toaster.infrastructure.querydsl;
//
// import static com.app.toaster.domain.QToast.*;
// import static com.app.toaster.fixture.Fixture.*;
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.app.toaster.config.JpaQueryFactoryConfig;
// import com.app.toaster.toast.domain.Toast;
// import com.app.toaster.fixture.Fixture;
// import com.app.toaster.infrastructure.CategoryRepository;
// import com.app.toaster.toast.infrastructure.ToastRepository;
// import com.app.toaster.user.infrastructure.UserRepository;
// import com.querydsl.core.types.dsl.BooleanExpression;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import jakarta.persistence.EntityManager;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @ExtendWith(SpringExtension.class)
// @DataJpaTest(showSql = true)
// @Import(JpaQueryFactoryConfig.class)
// class CustomToastRepositoryTest {
//
// 	CustomToastRepository customToastRepository;
// 	JPAQueryFactory jpaQueryFactory;
//
// 	@Autowired
// 	ToastRepository toastRepository;
//
// 	@Autowired
// 	UserRepository userRepository;
//
// 	@Autowired
// 	CategoryRepository categoryRepository;
//
// 	@Autowired
// 	EntityManager em;
//
// 	@BeforeEach
// 	void setup() {
// 		jpaQueryFactory = new JPAQueryFactory(em);
// 		customToastRepository = new CustomToastRepository(jpaQueryFactory);
//
// 		userRepository.save(USER_1);
// 		categoryRepository.save(CATEGORY_1);
// 		categoryRepository.save(CATEGORY_2);
// 		toastRepository.save(TOAST_1);
// 		toastRepository.save(TOAST_2);
// 		toastRepository.save(TOAST_3);
// 	}
// 	@AfterEach
// 	void tearDown() {
// 		// 테스트 후 정리 작업 수행
// 		em.clear();
// 		userRepository.deleteAll();
// 		categoryRepository.deleteAll();
// 		toastRepository.deleteAll();
// 	}
//
//
// 	@Nested
// 	@DisplayName("토스트 jpa repository test")
// 	class 토스트_JpaRepository_Test {
//
// 		@Nested
// 		@DisplayName("토스트 카테고리 jpa repository test")
// 		class 토스트_카테고리_jpa_test{
// 			@Test
// 			@DisplayName("토스트의 getAllByCategory test")
// 			void test_ToastJpaRepository_조회() {
//
// 				List<Toast> toastList = toastRepository.getAllByCategory(CATEGORY_1);
//
//
// 				Assertions.assertThat(toastList.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toastList.get(1).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				Assertions.assertThat(toastList.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
// 		}
// 		@Nested
// 		@DisplayName("토스트 jpa repository 검색 test")
// 		class 토스트_JPA_검색_test{
//
// 			@Test
// 			@DisplayName("토스트의 검색 쿼리 문자열이 앞에 있는 경우 test")
// 			void test_ToastJpaRepository_검색1(){
// 				List<Toast> toastList = toastRepository.searchToastsByQuery(USER_1.getUserId(),"검색");
//
// 				Assertions.assertThat(toastList.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toastList.get(1).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				Assertions.assertThat(toastList.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 			@Test
// 			@DisplayName("토스트의 검색 쿼리 문자열이 뒤에 있는 경우 test")
// 			void test_ToastJpaRepository_검색2(){
// 				List<Toast> toastList = toastRepository.searchToastsByQuery(USER_1.getUserId(),"되나");
//
// 				Assertions.assertThat(toastList.get(0).getId()).isEqualTo(TOAST_1.getId());
// 				Assertions.assertThat(toastList.get(1).getId()).isEqualTo(TOAST_3.getId());
// 				// Assertions.assertThat(toastList.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 		}
//
// 		@DisplayName("토스트 수정 관련 jpa test")
// 		@Nested
// 		class 토스트_JPA_수정_TEST{
// 			@Test
// 			@DisplayName("토스트 수정 쿼리 jpa test")
// 			@Transactional
// 			void updateCategoryIdsToNull(){
// 				//given
// 				List<Long> categoryIds = new ArrayList<>();
// 				categoryIds.add(CATEGORY_1.getCategoryId());
// 				categoryIds.add(CATEGORY_2.getCategoryId());
//
// 				//when
// 				toastRepository.updateCategoryIdsToNull(categoryIds);
//
// 				//then
// 				Assertions.assertThat(TOAST_1.getCategory() == null);
// 				Assertions.assertThat(TOAST_2.getCategory() == null);
// 				Assertions.assertThat(TOAST_3.getCategory() == null);
// 				Assertions.assertThat(TOAST_3_CATEGORY_2.getCategory() == null);
// 			}
// 		}
// 	}
// 	@Nested
// 	@DisplayName("토스트 querydsl test")
// 	class 토스트_QueryDSL_Test{
// 		@Nested
// 		@DisplayName("토스트 QueryDSL category test")
// 		class 토스트_QueryDSL_Category_Test {
//
// 			@Test
// 			@DisplayName("토스트의 queryDSL getAllByCategory test")
// 			void ToastQueryRepository_getAllByCategory_테스트() {
//
// 				List<Toast> toasts = customToastRepository.getAllByCategory(CATEGORY_1);
//
// 				Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toasts.get(1).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				Assertions.assertThat(toasts.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 			@Test
// 			@DisplayName("categoryid 결과를 찾지 못했을 때 test")
// 			void ToastQueryRepository_Category_id_2_테스트() {
//
// 				List<Toast> toasts = jpaQueryFactory.selectFrom(toast)
// 					.where(eqCategoryId(CATEGORY_2.getCategoryId()))
// 					.fetch();
// 				assertNotNull(toasts, "조회 데이터가 없습니다.");
// 				// Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				// Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 		}
//
// 		@Nested
// 		@DisplayName("토스트 QueryDSL search test")
// 		class 토스트_QueryDSL_검색쿼리_test{
// 			@Test
// 			@DisplayName("토스트의 queryDSL 문자열이 앞에 있을 때 쿼리 검색 test")
// 			@Transactional(readOnly = true)
// 			void ToastQueryRepository_searchToastsByQuery_테스트() {
//
// 				List<Toast> toasts = customToastRepository.searchToastsByQuery(USER_1.getUserId(), "검색");
//
// 				Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toasts.get(1).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				Assertions.assertThat(toasts.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 			@Test
// 			@DisplayName("토스트의 queryDSL 문자열이 뒤에 있을 때 쿼리 검색 test")
// 			@Transactional(readOnly = true)
// 			void ToastQueryRepository_searchToastsByQuery_테스트2() {
//
// 				List<Toast> toasts = customToastRepository.searchToastsByQuery(USER_1.getUserId(), "되나");
//
// 				Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toasts.get(1).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 				// Assertions.assertThat(toasts.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
//
// 			@Test
// 			@DisplayName("검색 쿼리가 비어있을 때")
// 			@Transactional(readOnly = true)
// 			void ToastQueryRepository_searchToastsByQuery_테스트3() {
//
// 				List<Toast> toasts = customToastRepository.searchToastsByQuery(USER_1.getUserId(), "");
//
// 				Assertions.assertThat(toasts.get(0).getId()).isEqualTo(Fixture.TOAST_1.getId());
// 				Assertions.assertThat(toasts.get(1).getId()).isEqualTo(Fixture.TOAST_2.getId());
// 				Assertions.assertThat(toasts.get(2).getId()).isEqualTo(Fixture.TOAST_3.getId());
// 			}
// 		}
// 		//적용하더라도 프로덕션 코드의 em부분 em.flush, em.clear체크.
//
// 		@Nested
// 		@DisplayName("토스트 수정 관련 querydsl test")
// 		class 토스트_QueryDSL_수정_TEST{
// 			@Test
// 			@DisplayName("토스트 수정 쿼리 querydsl test")
// 			@Transactional
// 			void updateCategoryIdsToNull(){
// 				//given
// 				List<Long> categoryIds = new ArrayList<>();
// 				categoryIds.add(CATEGORY_1.getCategoryId());
// 				categoryIds.add(CATEGORY_2.getCategoryId());
//
// 				//when
// 				customToastRepository.updateCategoryIdsToNull(categoryIds);
// 				//
// 				em.flush();
// 				em.clear();
//
// 				//then
// 				Assertions.assertThat(TOAST_1.getCategory() == null);
// 				Assertions.assertThat(TOAST_2.getCategory() == null);
// 				Assertions.assertThat(TOAST_3.getCategory() == null);
// 				Assertions.assertThat(TOAST_3_CATEGORY_2.getCategory() == null);
// 			}
// 		}
//
// 		@Nested
// 		@DisplayName("토스트 QueryDSL 안읽은 토스트 개수 세기 test")
// 		class 토스트_QueryDSL_안읽은_토스트_개수_세기_test{
//
// 			@Test
// 			@DisplayName("안읽은 토스트 개수 세기 쿼리 querydsl test")
// 			void countUnReadToast(){
// 				//given
// 				TOAST_1.updateIsRead(false);
// 				TOAST_2.updateIsRead(false);
// 				em.flush();
//
// 				//when
// 				Integer res = customToastRepository.getUnReadToastNumber(USER_1.getUserId());
// 				//
// 				//em.clear();
//
// 				//then
// 				Assertions.assertThat(res == 2);
// 			}
//
// 			@Test
// 			@DisplayName("안 읽은 토스트 개수가 없는 경우. querydsl test")
// 			void countUnReadToastAtNotFoundCase(){
// 				//given
// 				TOAST_1.updateIsRead(true);
// 				TOAST_2.updateIsRead(true);
// 				TOAST_3.updateIsRead(true);
// 				em.flush();
//
// 				//when
// 				Integer res = customToastRepository.getUnReadToastNumber(USER_1.getUserId());
// 				//
// 				//em.clear();
//
// 				//then
// 				Assertions.assertThat(res == 0);
// 			}
//
// 		}
// 	}
//
//
//
// 	private BooleanExpression eqCategoryId(Long id){
// 		return id!=null?toast.category.categoryId.eq(id):null;
// 	}
//
// }