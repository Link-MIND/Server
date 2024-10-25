package com.app.toaster.timer.infrastructure;

import com.app.toaster.category.domain.Category;
import com.app.toaster.timer.domain.Reminder;
import com.app.toaster.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TimerRepository extends JpaRepository<Reminder, Long> {

    ArrayList<Reminder> findAllByUser(User user);

    void deleteAllByUser(User user);

    ArrayList<Reminder> findAllByCategoryAndUser(Category category, User user);

    Reminder findByCategory_CategoryId(Long categoryId);

    @Query("select r.category from Reminder r where r.id = :id")
    Category findCategoryByReminderId(@Param("id") Long reminderId);


}