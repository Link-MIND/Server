package com.app.toaster.category.repository;

import com.app.toaster.ToasterApplication;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.user.domain.User;
import com.app.toaster.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //mysql을 쓸 때는 이렇게 none으로 추가해줘야한다.
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("category 우선순위 수정이 잘되는지 확인한다.")
    void updateCategoryPriority(){
        categoryRepository.increasePriorityByOne(1L,1,3, 126L);
    }

    @Test
    @DisplayName("category를 우선순위별로 정렬하여 가져올때 리마인더를 가져오는지 확인한다.")
    void getCategoryPriorityWithReminder(){
        User user = userRepository.findByUserId(126L).orElseThrow();

        categoryRepository.findAllByUserOrderByPriority(user);
    }
}
