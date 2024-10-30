package com.app.toaster.category.infrastructure;


import java.util.ArrayList;
import java.util.List;


import com.app.toaster.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.toaster.category.domain.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT COALESCE(MAX(c.priority), 0) FROM Category c WHERE c.user = :user")
    int findMaxPriorityByUser(@Param("user") User user);

    ArrayList<Category> findAllByUserOrderByPriority(User user);

    ArrayList<Category> findAllByUser(User user);

    ArrayList<Category> findTop3ByUserOrderByLatestReadTimeDesc(User user);

    @Modifying
    @Query("UPDATE Category c SET c.title = :newTitle WHERE c.categoryId = :categoryId")
    void updateCategoryTitle(@Param("categoryId") Long categoryId, @Param("newTitle") String newTitle);

    @Modifying
    @Query("UPDATE Category c SET c.priority = c.priority - 1 " +
            "WHERE c.categoryId != :categoryId AND c.user.userId =:userId AND c.priority > :currentPriority AND c.priority <= :newPriority")
    void decreasePriorityByOne(@Param("categoryId") Long categoryId,
                               @Param("currentPriority") int currentPriority,
                               @Param("newPriority") int newPriority,
                               @Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Category c SET c.priority = c.priority + 1 " +
            "WHERE c.categoryId != :categoryId AND c.user.userId =:userId AND c.priority >= :newPriority AND c.priority < :currentPriority")
    void increasePriorityByOne(@Param("categoryId") Long categoryId,
                               @Param("currentPriority") int currentPriority,
                               @Param("newPriority") int newPriority,
                               @Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Category c SET c.priority = c.priority - 1 " +
            "WHERE c.categoryId != :categoryId AND c.user.userId =:userId AND c.priority > :currentPriority")
    void decreasePriorityNextDeleteCategory(@Param("categoryId") Long categoryId, @Param("currentPriority") int currentPriority,@Param("userId")Long userId);

    @Query("SELECT c FROM Category c WHERE " +
      "c.user.userId = :userId and " +
      "c.title LIKE CONCAT('%',:query, '%')"
    )
    List<Category> searchCategoriesByQuery(Long userId,String query);

    void deleteAllByUser(User user);

    Boolean existsCategoriesByUserAndTitle(User user, String title);

    Long countAllByUser(User user);

    Long countAllByTitleAndUser(String title, User user);



}
