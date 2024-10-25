package com.app.toaster.service.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.toaster.common.dto.ApiResponse;
import com.app.toaster.controller.response.search.CategoryResult;
import com.app.toaster.controller.response.search.SearchMainResult;
import com.app.toaster.toast.controller.response.ToastDto;
import com.app.toaster.domain.Category;
import com.app.toaster.toast.domain.Toast;
import com.app.toaster.user.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.Success;
import com.app.toaster.exception.model.BadRequestException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.infrastructure.CategoryRepository;
import com.app.toaster.toast.infrastructure.ToastRepository;
import com.app.toaster.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final ToastRepository toastRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public ApiResponse<SearchMainResult> searchMain(Long userId, String searchParam){
		if(searchParam.isBlank()){
			throw new BadRequestException(Error.BAD_REQUEST_VALIDATION, Error.BAD_REQUEST_URL.getMessage());
		}

		User presentUser =  userRepository.findByUserId(userId).orElseThrow(
			()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
		);
		List<Toast> searchToastList = toastRepository.searchToastsByQuery(userId, searchParam);
		List<Category> searchCategoryList = categoryRepository.searchCategoriesByQuery(userId, searchParam);

		if (searchToastList.isEmpty() && searchCategoryList.isEmpty()){
			return ApiResponse.success(Success.SEARCH_SUCCESS_BUT_IS_EMPTY,null);
		}
		return ApiResponse.success(Success.SEARCH_SUCCESS, SearchMainResult.of(searchParam,
			searchToastList.stream().map(
					toast -> ToastDto.of(toast))
				.collect(Collectors.toList()),
			searchCategoryList.stream().map(
					category -> CategoryResult.of(category.getCategoryId(), category.getTitle(),toastRepository.countAllByCategory(category)))
				.collect(Collectors.toList())
			));
	}

}
