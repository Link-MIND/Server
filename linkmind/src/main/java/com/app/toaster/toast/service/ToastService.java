package com.app.toaster.toast.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.app.toaster.toast.controller.request.MoveToastDto;
import com.app.toaster.toast.controller.response.ModifiedCategory;
import com.app.toaster.toast.controller.response.ToastDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.toaster.toast.controller.request.IsReadDto;
import com.app.toaster.toast.controller.request.SaveToastDto;
import com.app.toaster.toast.controller.request.UpdateToastDto;
import com.app.toaster.parse.controller.response.OgResponse;
import com.app.toaster.toast.controller.response.IsReadResponse;
import com.app.toaster.toast.controller.response.ModifiedTitle;
import com.app.toaster.category.domain.Category;
import com.app.toaster.toast.domain.Toast;
import com.app.toaster.user.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.BadRequestException;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.ForbiddenException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.external.client.aws.S3Service;
import com.app.toaster.category.infrastructure.CategoryRepository;
import com.app.toaster.toast.infrastructure.ToastRepository;
import com.app.toaster.user.infrastructure.UserRepository;
import com.app.toaster.parse.service.ParsingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToastService {
	private final UserRepository userRepository;
	private final ToastRepository toastRepository;
	private final CategoryRepository categoryRepository;
	private final S3Service s3Service;
	private final ParsingService parsingService;

	@Value("${static-image.root}")
	private String BASIC_ROOT;


	@Transactional
	public void createToast(Long userId, SaveToastDto saveToastDto){
		//해당 유저 탐색
		User presentUser = findUser(userId);
		//토스트 생성
		try {
			System.out.println(saveToastDto.linkUrl());
			OgResponse res = parsingService.getOg(saveToastDto.linkUrl());
			//byte 배열로 읽어들임.
			log.info(res.titleAdvanced());
			log.info(res.imageAdvanced());
			String imageString = checkIsBasicImage(res.imageAdvanced());
			// // ImagePresignedUrlResponse realRes = getUploadPreSignedUrl(res.imageAdvanced());
			// log.info(realRes.fileName());
			// log.info(realRes.preSignedUrl());

			//presigned url
			Toast toast = Toast.builder()
				.user(presentUser)
				.linkUrl(saveToastDto.linkUrl())
				.title(res.titleAdvanced())
				.thumbnailUrl(imageString)
				.build();
			// 만약 유저에게 만들어져있는 카테고리가 없는지 확인하고
			checkCategoryIsEmpty(toast, saveToastDto.categoryId());
			toastRepository.save(toast);
		} catch (IOException e ) {	//여기서 에러 발생 시 외부 s3 문제일 수 도 있으므로 500으로 에러 예상 범위 알림.
			throw new CustomException(Error.CREATE_TOAST_PROCCESS_EXCEPTION, Error.CREATE_TOAST_PROCCESS_EXCEPTION.getMessage());
		}

	}
	@Transactional
	public IsReadResponse readToast(Long userId, IsReadDto isReadDto){
		Toast toast = findToast(isReadDto.toastId());
		isOwnerOfToast(userId,toast);
		if (isReadDto.isRead() && toast.getIsRead() || !isReadDto.isRead() && !toast.getIsRead()){
			throw new BadRequestException(Error.BAD_REQUEST_ISREAD, Error.BAD_REQUEST_ISREAD.getMessage());
		}

		if(isReadDto.isRead())
			toast.setUpdateAt();

		toast.updateIsRead(isReadDto.isRead());

		return IsReadResponse.of(isReadDto.isRead());
	}

	@Transactional(readOnly = true)
	public List<ToastDto> getToastTop3_savedRecently(Long userId){
		User presentUser =  findUser(userId);
        return toastRepository.findTop3ByUserOrderByCreatedAtDesc(presentUser)
				.stream()
				.map(ToastDto::of)
				.toList();
	}

	@Transactional
	public void deleteToast(Long userId, Long toastId) {
		User presentUser =  userRepository.findByUserId(userId).orElseThrow(
			()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
		);
		Toast toast = toastRepository.findById(toastId).orElseThrow(
			() -> new NotFoundException(Error.NOT_FOUND_TOAST_EXCEPTION, Error.NOT_FOUND_TOAST_EXCEPTION.getMessage())
		);
		if (!presentUser.equals(toast.getUser())){
			throw new ForbiddenException(Error.UNAUTHORIZED_ACCESS, Error.UNAUTHORIZED_ACCESS.getMessage());
		}
		// BASIC_ROOT를 제외한 문자열을 추출합니다.
		// String imageKey = toast.getThumbnailUrl().replace(BASIC_ROOT, "");
		// s3Service.deleteImage(imageKey);
		toastRepository.deleteById(toastId);
	}

	@Transactional
	public void deleteAllToast(User user) throws IOException {
		List<Toast> toasts = toastRepository.getAllByUser(user);
		// // BASIC_ROOT를 제외한 문자열을 추출합니다.
		// List<String> imageKeyList = toasts.stream().map(
		// 	(toast) -> toast.getThumbnailUrl().replace(BASIC_ROOT, ""))
		// 	.collect(Collectors.toList());
		// s3Service.deleteImages(imageKeyList);
		toastRepository.deleteAllById(toasts.stream().map(
			(Toast::getId)).collect(Collectors.toList()));
	}

	@Transactional
	public ModifiedTitle modifyTitle(Long userId, UpdateToastDto updateToastDto){
		Toast toast = findToast(updateToastDto.toastId());
		isOwnerOfToast(userId,toast);
		toast.updateTitle(updateToastDto.title());
		return ModifiedTitle.of(updateToastDto.title());
	}

	@Transactional
	public ModifiedCategory modifyClip(Long userId, MoveToastDto updateToastDto){
		Toast toast = findToast(updateToastDto.toastId());
		isOwnerOfToast(userId, toast);
		Category category = findCategory(updateToastDto.categoryId());
		toast.updateCategory(category);
		return ModifiedCategory.of(updateToastDto.categoryId());
	}

	private void isOwnerOfToast(Long userId, Toast toast){
		User presentUser = findUser(userId);
		if (!toast.isToastOwner(presentUser)){
			throw new ForbiddenException(Error.UNAUTHORIZED_ACCESS, Error.UNAUTHORIZED_ACCESS.getMessage());
		}
	}

	private User findUser(Long userId){
		return userRepository.findByUserId(userId).orElseThrow(
				()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
		);
	}

	private Toast findToast(Long toastId){
		return toastRepository.findById(toastId).orElseThrow(
				() -> new BadRequestException(Error.BAD_REQUEST_ID, Error.BAD_REQUEST_ID.getMessage())
		);
	}

	private Category findCategory(Long categoryId){
		return categoryRepository.findById(categoryId).orElseThrow(
				() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION, Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage())
		);
	}



	private void checkCategoryIsEmpty(Toast toast, Long categoryId){
		if (categoryId == null) {
			toast.updateCategory(null);
		} else { //원래 있던 곳에서 카테고리 고르면 카테고리 아이디로 검색 후 그것을 넣음.
			Category foundCategory = categoryRepository.findById(categoryId).orElseThrow(
				() -> new CustomException(Error.NOT_FOUND_CATEGORY_EXCEPTION,
					Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));
			toast.updateCategory(foundCategory);
		}
	}

	private String checkIsBasicImage(String imageUrl){
		if (!imageUrl.startsWith("http")){
			return BASIC_ROOT+imageUrl;
		}
		return imageUrl;
	}




}
