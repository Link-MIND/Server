package com.app.toaster.category.service;

import com.app.toaster.category.controller.request.ChangeCategoryPriorityDto;
import com.app.toaster.category.controller.request.ChangeCateoryTitleDto;
import com.app.toaster.category.controller.request.CreateCategoryDto;
import com.app.toaster.category.controller.response.CategoriesResponse;
import com.app.toaster.category.controller.response.DuplicatedResponse;
import com.app.toaster.toast.controller.response.ToastDto;
import com.app.toaster.toast.controller.response.ToastFilter;
import com.app.toaster.category.controller.response.CategoryResponse;
import com.app.toaster.category.controller.response.GetCategoryResponseDto;
import com.app.toaster.category.domain.Category;
import com.app.toaster.timer.domain.Reminder;
import com.app.toaster.toast.domain.Toast;
import com.app.toaster.user.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.BadRequestException;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.timer.infrastructure.TimerRepository;
import com.app.toaster.toast.infrastructure.ToastRepository;
import com.app.toaster.user.infrastructure.UserRepository;

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

	private final static int MAX_CATEGORY_NUMBER = 15;
	private final TimerRepository timerRepository;

	@Transactional
	public void createCategory(final Long userId, final CreateCategoryDto createCategoryDto) {
		User presentUser = findUser(userId);

		// 현재 유저의 최대 우선순위를 가져와서 새로운 우선순위를 설정
		val maxPriority = categoryRepository.findMaxPriorityByUser(presentUser);

		val categoryNum = categoryRepository.countAllByUser(presentUser);

		if (categoryNum >= MAX_CATEGORY_NUMBER) {
			throw new CustomException(Error.BAD_REQUEST_CREATE_CLIP_EXCEPTION,
					Error.BAD_REQUEST_CREATE_CLIP_EXCEPTION.getMessage());
		}

		if(categoryRepository.countAllByTitleAndUser(createCategoryDto.categoryTitle(),presentUser)>0){
			throw new CustomException(Error.UNPROCESSABLE_CREATE_TIMER_EXCEPTION, Error.UNPROCESSABLE_CREATE_TIMER_EXCEPTION.getMessage());
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
	public void deleteCategory(final ArrayList<Long> deleteCategoryDto) {
		if (deleteCategoryDto.isEmpty()) {
			throw new BadRequestException(Error.BAD_REQUEST_VALIDATION, Error.BAD_REQUEST_VALIDATION.getMessage());
		}

		toastRepository.updateCategoryIdsToNull(deleteCategoryDto);

		for (Long categoryId : deleteCategoryDto) {
			Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION,
							Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

			categoryRepository.decreasePriorityNextDeleteCategory(categoryId, category.getPriority(),
					category.getUser().getUserId());

			Reminder timer = timerRepository.findByCategory_CategoryId(categoryId);
			if (timer != null)
				timerRepository.delete(timer);
			categoryRepository.delete(category);
		}

	}

	@Transactional(readOnly = true)
	public CategoriesResponse getCategories(final Long userId) {
		User presentUser = findUser(userId);

		return CategoriesResponse.of(toastRepository.countAllByUser(presentUser),
				categoryRepository.findAllByUserOrderByPriority(presentUser)
						.stream()
						.map(category -> CategoryResponse.builder()
								.categoryId(category.getCategoryId())
								.categoryTitle(category.getTitle())
								.toastNum(toastRepository.getAllByCategory(category).size()).build()
						).collect(Collectors.toList()));

	}

	@Transactional
	public GetCategoryResponseDto getCategory(final Long userId, final Long categoryId, final ToastFilter filter) {
		User presentUser = findUser(userId);
		if (categoryId == 0) {
			List<Toast> toastAllList = toastRepository.getAllByUserOrderByCreatedAtDesc(presentUser);
			List<ToastDto> toastListDto = mapToToastDtoList(toastAllList, filter, null);
			return GetCategoryResponseDto.builder()
					.allToastNum(countToToast(filter,null,presentUser,true))
					.toastListDto(toastListDto)
					.build();
		}

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION,
						Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

		category.updateLatestReadTime(LocalDateTime.now());

		ArrayList<Toast> toasts = toastRepository.getAllByCategory(category);
		List<ToastDto> toastListDto = mapToToastDtoList(toasts, filter, category);

		return GetCategoryResponseDto.builder()
				.allToastNum(countToToast(filter,category,presentUser,false))
				.toastListDto(toastListDto)
				.build();
	}

	//순서 업데이트
	@Transactional
	public void editCategoryPriority(ChangeCategoryPriorityDto changeCategoryPriorityDto) {

		val newPriority = changeCategoryPriorityDto.newPriority();

		Category category = categoryRepository.findById(changeCategoryPriorityDto.categoryId())
				.orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION,
						Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));

		int currentPriority = category.getPriority();
		category.updateCategoryPriority(changeCategoryPriorityDto.newPriority());

		if (currentPriority < newPriority)
			categoryRepository.decreasePriorityByOne(changeCategoryPriorityDto.categoryId(), currentPriority,
					newPriority, category.getUser().getUserId());
		else if (currentPriority > newPriority)
			categoryRepository.increasePriorityByOne(changeCategoryPriorityDto.categoryId(), currentPriority,
					newPriority, category.getUser().getUserId());

	}

	@Transactional
	public void editCategoryTitle(ChangeCateoryTitleDto changeCateoryTitleDto) {

		categoryRepository.updateCategoryTitle(changeCateoryTitleDto.categoryId(), changeCateoryTitleDto.newTitle());
	}

	//해당 유저 탐색
	private User findUser(Long userId) {
		return userRepository.findByUserId(userId).orElseThrow(
				() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
		);
	}

	@Transactional(readOnly = true)
	public DuplicatedResponse checkDuplicatedTitle(Long userId, String title) {
		return DuplicatedResponse.of(categoryRepository.existsCategoriesByUserAndTitle(findUser(userId), title));

	}

	private List<ToastDto> mapToToastDtoList(List<Toast> toasts, ToastFilter filter, Category category) {
		Stream<Toast> toastStream = switch (filter) {
			case ALL -> toasts.stream();
			case READ -> toasts.stream().filter(Toast::getIsRead);
			case UNREAD -> toasts.stream().filter(toast -> !toast.getIsRead());
			default -> throw new NotFoundException(Error.NOT_FOUND_TOAST_FILTER, Error.NOT_FOUND_TOAST_FILTER.getMessage());
		};

		return toastStream.map(ToastDto::of).toList();
	}

	private int countToToast(ToastFilter filter, Category category,User user, boolean isCategoryNull) {
		if (!isCategoryNull)
			return switch (filter) {
				case ALL -> toastRepository.countAllByCategory(category).intValue();
				case READ -> toastRepository.countAllByCategoryAndIsReadTrue(category).intValue();
                case UNREAD -> toastRepository.countAllByCategoryAndIsReadFalse(category).intValue();
			};
        return switch (filter){
            case ALL -> toastRepository.countAllByUser(user).intValue();
            case READ -> toastRepository.countALLByUserAndIsReadTrue(user).intValue();
            case UNREAD -> toastRepository.countALLByUserAndIsReadFalse(user).intValue();
        };
	}

}
