package com.app.toaster.category.service;

import com.app.toaster.category.controller.request.ChangeCateoryPriorityDto;
import com.app.toaster.category.domain.Category;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.category.service.CategoryService;
import com.app.toaster.user.infrastructure.UserRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //mysql을 쓸 때는 이렇게 none으로 추가해줘야한다.
//@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private static final ArrayList<ChangeCateoryPriorityDto> list = new ArrayList<>();


    @BeforeEach
    void preMakeList() {
        Random random = new Random();
        list.add(new ChangeCateoryPriorityDto(random.nextLong(1, 5), random.nextInt(1, 5)));
        list.add(new ChangeCateoryPriorityDto(random.nextLong(1, 5), random.nextInt(1, 5)));
    }

    @AfterEach
    void postList() {
        list.clear();
    }


    @Test
    @DisplayName("category 우선순위 동시 수정을 무작위로 진행 했을 때 정합성이 유지된다.")
    void categoryDeadLock() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executorService.execute(() -> {
            try {
                categoryService.editCategoryPriority(126L, list.get(0));
            } finally {
                latch.countDown();
            }
        }); //우선순위 순 -> 3 1 2 4  id순 ->     2,3,1,4
        executorService.execute(() -> {
            try {
                categoryService.editCategoryPriority(126L, list.get(1));
            } finally {
                latch.countDown();
            }
        }); //3,1,4,2
        latch.await();

        ArrayList<Category> newList = categoryRepository.findAllByUserOrderByPriority(userRepository.findByUserId(126L).get());
        HashSet<Integer> answer = newList.stream()
                .map(Category::getPriority)
                .collect(Collectors.toCollection(HashSet::new));

        Assertions.assertEquals(4, answer.size()); //충돌 시 어떤 것이 바뀌었든, 같은 숫자의 우선순위가 들어가지 않는다.
    }

    @Test
    @DisplayName("category 우선순위 수정을 동시에 진행 하지않았을 때도 정합성이 유지된다.")
    void categoryDeadLockValid() throws InterruptedException {
        ArrayList<ChangeCateoryPriorityDto> list = new ArrayList<>();
        list.add(new ChangeCateoryPriorityDto(1L, 3));
        list.add(new ChangeCateoryPriorityDto(1L, 4));

        categoryService.editCategoryPriority(126L, list.get(0));
        //2,3,1,4
        categoryService.editCategoryPriority(126L, list.get(1));
        //3,1,4,2
        ArrayList<Category> newList = categoryRepository.findAllByUserOrderByPriority(userRepository.findByUserId(126L).get());
        HashSet<Integer> answer = newList.stream()
                .map(Category::getPriority)
                .collect(Collectors.toCollection(HashSet::new));

        Assertions.assertEquals(4, answer.size()); //아 마지막꺼만 적용되나본데?
    }


}
