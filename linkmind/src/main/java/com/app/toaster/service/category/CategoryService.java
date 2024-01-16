package com.app.toaster.service.category;

import com.app.toaster.controller.request.category.*;
import com.app.toaster.controller.response.category.CategoriesResponse;
import com.app.toaster.controller.response.category.DuplicatedResponse;
import com.app.toaster.controller.response.toast.ToastDto;
import com.app.toaster.controller.response.toast.ToastFilter;
import com.app.toaster.controller.response.category.CategoryResponse;
import com.app.toaster.controller.response.category.GetCategoryResponseDto;
import com.app.toaster.domain.Category;
import com.app.toaster.domain.Reminder;
import com.app.toaster.domain.Toast;
import com.app.toaster.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.infrastructure.CategoryRepository;
import com.app.toaster.infrastructure.TimerRepository;
import com.app.toaster.infrastructure.ToastRepository;
import com.app.toaster.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ToastRepository toastRepository;

    private final static int MAX_CATERGORY_NUMBER = 50;
    private final TimerRepository timerRepository;

    @Transactional
    public void createCategory(final Long userId, final CreateCategoryDto createCategoryDto){
        User presentUser = findUser(userId);

        // 현재 유저의 최대 우선순위를 가져와서 새로운 우선순위를 설정
        val maxPriority = categoryRepository.findMaxPriorityByUser(presentUser);

        val categoryNum= categoryRepository.findAllByUser(presentUser).size();

        if(categoryNum >= MAX_CATERGORY_NUMBER){
            throw new CustomException(Error.UNPROCESSABLE_ENTITY_CREATE_CLIP_EXCEPTION, Error.UNPROCESSABLE_ENTITY_CREATE_CLIP_EXCEPTION.getMessage());
        }

        //카테고리 생성
        Category newCategory = Category.builder()
                .title(createCategoryDto.categoryTitle())
                .user(presentUser)
                .priority(maxPriority + 1)
                .build();

        categoryRepository.save(newCategory);
    }

    @Transactional
    public void deleteCategory(final DeleteCategoryDto deleteCategoryDto){

        toastRepository.updateCategoryIdsToNull(deleteCategoryDto.deleteCategoryList());

        for (Long categoryId : deleteCategoryDto.deleteCategoryList()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION, Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

            categoryRepository.decreasePriorityNextDeleteCategory(categoryId, category.getPriority());

            Reminder timer = timerRepository.findByCategory_CategoryId(categoryId);
            if(timer != null)
                timerRepository.delete(timer);
            categoryRepository.delete(category);
        }

    }

    public CategoriesResponse getCategories(final Long userId){
        User presentUser = findUser(userId);

        return CategoriesResponse.of(toastRepository.countAllByUser(presentUser),categoryRepository.findAllByUserOrderByPriority(presentUser)
            .stream()
            .map(category -> CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getTitle())
                .toastNum(toastRepository.getAllByCategory(category).size()).build()
            ).collect(Collectors.toList()));


    }

    public GetCategoryResponseDto getCategory(final Long userId, final Long categoryId, final ToastFilter filter) {
        User presentUser = findUser(userId);
        if (categoryId ==0){
            List<Toast> toastAllList = toastRepository.getAllByUser(presentUser);
            List<ToastDto> toastListDto = mapToToastDtoList(toastAllList, filter, null);
            return GetCategoryResponseDto.builder()
                .allToastNum(toastAllList.size())
                .toastListDto(toastListDto)
                .build();
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION, Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

        category.updateLatestReadTime(LocalDateTime.now());

        ArrayList<Toast> toasts = toastRepository.getAllByCategory(category);
        List<ToastDto> toastListDto = mapToToastDtoList(toasts, filter, category);

        return GetCategoryResponseDto.builder()
            .allToastNum(toasts.size())
            .toastListDto(toastListDto)
            .build();
    }

    //순서 업데이트
    @Transactional
    public void editCategoryPriority(ChangeCateoryPriorityDto changeCateoryPriorityDto){

        val newPriority = changeCateoryPriorityDto.newPriority();

        Category category = categoryRepository.findById(changeCateoryPriorityDto.categoryId())
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION, Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

        int currentPriority = category.getPriority();
        category.updateCategoryPriority(changeCateoryPriorityDto.newPriority());

        if(currentPriority < newPriority)
            categoryRepository.decreasePriorityByOne(changeCateoryPriorityDto.categoryId(), currentPriority, newPriority);
        else if (currentPriority > newPriority)
            categoryRepository.increasePriorityByOne(changeCateoryPriorityDto.categoryId(), currentPriority, newPriority);


    }

    @Transactional
    public void editCategoryTitle(ChangeCateoryTitleDto changeCateoryTitleDto){

        categoryRepository.updateCategoryTitle(changeCateoryTitleDto.categoryId(), changeCateoryTitleDto.newTitle());
    }

    //해당 유저 탐색
    private User findUser(Long userId){
        return userRepository.findByUserId(userId).orElseThrow(
                ()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
        );
    }

    private List<ToastDto> mapToToastDtoList(List<Toast> toasts, ToastFilter filter, Category category) {
        Stream<Toast> toastStream = switch (filter) {
            case ALL -> toasts.stream();
            case READ -> toastRepository.findByIsReadAndCategory(true, category).stream();
            case UNREAD -> toastRepository.findByIsReadAndCategory(false, category).stream();
            default ->
                    throw new NotFoundException(Error.NOT_FOUND_TOAST_FILTER, Error.NOT_FOUND_TOAST_FILTER.getMessage());
        };

        return toastStream.map(ToastDto::of).toList();
    }

    public DuplicatedResponse checkDuplicatedTitle(Long userId, String title){
        return DuplicatedResponse.of(categoryRepository.existsCategoriesByUserAndTitle(findUser(userId), title));

    }
}
