package com.app.toaster.toast.infrastructure;

import com.app.toaster.domain.Category;
import com.app.toaster.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.toaster.toast.domain.Toast;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ToastRepository extends JpaRepository<Toast, Long> {
	ArrayList<Toast> getAllByCategory(Category category);

	ArrayList<Toast> findByIsReadAndCategory(Boolean isRead, Category category);

	ArrayList<Toast> getAllByUser(User user);

	List<Toast> getAllByUserOrderByCreatedAtDesc(User user);

	ArrayList<Toast> getAllByUserAndIsReadIsTrue(User user);

	@Modifying
	@Query("UPDATE Toast t SET t.category = null WHERE t.category.categoryId IN :categoryIds")
	void updateCategoryIdsToNull(@Param("categoryIds") List<Long> categoryIds);

	@Query("SELECT t FROM Toast t WHERE " +
			"t.user.userId = :userId and " +
			"t.title LIKE CONCAT('%',:query, '%')"
	)
	List<Toast> searchToastsByQuery(Long userId, String query);

	Long countAllByUser(User user);

	Long countALLByUserAndIsReadTrue(User user);

	Long countALLByUserAndIsReadFalse(User user);

	Long countAllByCategory(Category category);

	Long countAllByCategoryAndIsReadTrue(Category category);

	Long countAllByCategoryAndIsReadFalse(Category category);

	@Query("SELECT COUNT(t) FROM Toast t WHERE t.user.userId = :userId AND t.isRead = false")
	Integer getUnReadToastNumber(Long userId);


  @Query("SELECT COUNT(t) FROM Toast t WHERE t.user=:user AND t.createdAt >= :startOfWeek AND t.createdAt <= :endOfWeek")
  Long countAllByCreatedAtThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek,
                            @Param("endOfWeek") LocalDateTime endOfWeek, @Param("user") User user);

  @Query("SELECT COUNT(t) FROM Toast t WHERE t.user=:user AND t.isRead = true AND t.updateAt >= :startOfWeek AND t.updateAt <= :endOfWeek")
  Long countAllByUpdateAtThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek,
                                     @Param("endOfWeek") LocalDateTime endOfWeek,
                                        @Param("user") User user);

	List<Toast> findTop3ByUserOrderByCreatedAtDesc(User user);

}
