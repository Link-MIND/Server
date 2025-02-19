package com.app.toaster.category.infrastructure;

import java.util.ArrayList;
import java.util.List;

import com.app.toaster.common.config.PessimisticWriteLock;
import com.app.toaster.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.toaster.category.domain.Category;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("SELECT COALESCE(MAX(c.priority), 0) FROM Category c WHERE c.user = :user")
	int findMaxPriorityByUser(@Param("user") User user);

	ArrayList<Category> findAllByUserOrderByPriority(User user);

	ArrayList<Category> findAllByUser(User user);

	ArrayList<Category> findTop3ByUserOrderByLatestReadTimeDesc(User user);

	@Modifying
	@Query("UPDATE Category c SET c.title = :newTitle WHERE c.categoryId = :categoryId")
	void updateCategoryTitle(@Param("categoryId") Long categoryId, @Param("newTitle") String newTitle);

	@PessimisticWriteLock
	@Query("""
    SELECT c
    FROM Category c, Category targetCategory
    WHERE targetCategory.categoryId = :categoryId
    AND c.user.userId = targetCategory.user.userId
    AND c.priority BETWEEN LEAST(:newPriority, targetCategory.priority)
                       AND GREATEST(:newPriority, targetCategory.priority)
    ORDER BY c.priority
    """)
	List<Category> findCategoriesForUpdate(
		@Param("categoryId") Long categoryId,
		@Param("newPriority") int newPriority
	);

	@Modifying
	@Query("UPDATE Category c SET c.priority = c.priority - 1 " +
		"WHERE c.categoryId != :categoryId AND c.user.userId =:userId AND c.priority > :currentPriority")
	void decreasePriorityNextDeleteCategory(
		@Param("categoryId") Long categoryId,
		@Param("currentPriority") int currentPriority,
		@Param("userId") Long userId
	);

	@Query("SELECT c FROM Category c WHERE " +
		"c.user.userId = :userId and " +
		"c.title LIKE CONCAT('%',:query, '%')"
	)
	List<Category> searchCategoriesByQuery(Long userId, String query);

	void deleteAllByUser(User user);

	Boolean existsCategoriesByUserAndTitle(User user, String title);

	Long countAllByUser(User user);

	Long countAllByTitleAndUser(String title, User user);

}
