// package com.app.toaster.infrastructure.querydsl;
//
// import static com.app.toaster.domain.QToast.*;
// import static com.app.toaster.domain.QUser.*;
//
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
//
// import com.app.toaster.domain.Category;
// import com.app.toaster.domain.QToast;
// import com.app.toaster.domain.QUser;
// import com.app.toaster.toast.domain.Toast;
// import com.app.toaster.user.domain.User;
// import com.querydsl.core.types.Path;
// import com.querydsl.core.types.dsl.BooleanExpression;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import jakarta.persistence.EntityManager;
// import lombok.RequiredArgsConstructor;
//
// @Repository
// @RequiredArgsConstructor
// public class CustomToastRepository {
// 	private final JPAQueryFactory queryFactory;
// 	// private final EntityManager em;
// 	public List<Toast> getAllByCategory(Category category){
// 		return queryFactory.select(toast)
// 			.from(toast)
// 			.where(eqCategoryId(category.getCategoryId()))
// 			.fetch();
// 	}
//
// 	private BooleanExpression eqCategoryId(Long id){
// 		if (id == null){
// 			return null;
// 		}
// 		return toast.category.categoryId.eq(id);
// 	}
//
// 	ArrayList<Toast> findByIsReadAndCategory(Boolean isRead, Category category){
// 		return null;
// 	};
//
// 	ArrayList<Toast> getAllByUser(User user){
// 		return null;
// 	}
//
// 	List<Toast> getAllByUserOrderByCreatedAtDesc(User user){
// 		return null;
// 	}
//
//
// 	ArrayList<Toast> getAllByUserAndIsReadIsTrue(User user){
// 		return null;
// 	}
//
//
// 	// @Modifying
// 	// @Query("UPDATE Toast t SET t.category = null WHERE t.category.categoryId IN :categoryIds")
// 	// void updateCategoryIdsToNull(@Param("categoryIds") List<Long> categoryIds){
// 	// }
//
// 	//querydsl의 수정은 bulkupdate라 영속성컨텍스트를 안거치기 때문에 무조건 flush,clear해주자.
// 	void updateCategoryIdsToNull(List<Long> categoryIds){
// 		queryFactory.update(toast)
// 			.set(toast.category, (Category)null)
// 			.where(toast.category.categoryId.in(categoryIds))
// 			.execute();
// 		// em.flush(); //test 코드에서는 따로 em을 주입하는 중이므로 테스트 후 넣자.
// 		// em.clear();
// 	}
//
//
// 	List<Toast> searchToastsByQuery(Long userId, String query){
// 		return queryFactory.select(toast)
// 			.from(toast)
// 			.leftJoin(toast.user, user).fetchJoin()
// 			.where(eqToastOwner(userId), containToastTitle(query))
// 			.fetch();
// 	}
// 	private BooleanExpression eqToastOwner(Long userId){
// 		return userId != null?toast.user.userId.eq(userId):null;
// 	}
//
// 	private BooleanExpression containToastTitle(String query){
// 		if (query == null || query.isEmpty() || query.isBlank()){
// 			return null;
// 		}
// 		return toast.title.containsIgnoreCase(query);
// 	}
//
//
//
// 	Long countAllByUser(User user){
// 		return null;
// 	}
//
//
// 	Long countALLByUserAndIsReadTrue(User user){
// 		return null;
// 	}
//
//
// 	Long countALLByUserAndIsReadFalse(User user){
// 		return null;
// 	}
//
//
// 	Long countAllByCategory(Category category){
// 		return null;
// 	}
//
//
// 	Long countAllByCategoryAndIsReadTrue(Category category){
// 		return null;
// 	}
//
//
// 	Long countAllByCategoryAndIsReadFalse(Category category){
// 		return null;
// 	}
//
//
// 	Integer getUnReadToastNumber(Long userId){
//
// 		Integer count = queryFactory.select(toast.count().intValue())
// 			.from(toast)
// 			.where(eqToastOwner(userId).and(toast.isRead.isFalse()))
// 			.fetchOne();
// 		return (count!=null)?count:0;
// 	}
//
//
// 	@Query("SELECT COUNT(t) FROM Toast t WHERE t.user=:user AND t.createdAt >= :startOfWeek AND t.createdAt <= :endOfWeek")
// 	Long countAllByCreatedAtThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek,
// 		@Param("endOfWeek") LocalDateTime endOfWeek, @Param("user") User user){
// 		return null;
// 	}
//
//
// 	@Query("SELECT COUNT(t) FROM Toast t WHERE t.user=:user AND t.isRead = true AND t.updateAt >= :startOfWeek AND t.updateAt <= :endOfWeek")
// 	Long countAllByUpdateAtThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek,
// 		@Param("endOfWeek") LocalDateTime endOfWeek,
// 		@Param("user") User user){
// 		return null;
// 	}
//
//
// }
