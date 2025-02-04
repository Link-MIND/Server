package com.app.toaster.external.client.aws;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.BadRequestException;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.NotFoundException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@Slf4j
public class S3Service {
	private final String bucketName;
	private final AWSConfig awsConfig;

	private static final Long MAX_FILE_SIZE = 5 * 1024 * 1024L;
	private static final Long PRE_SIGNED_URL_EXPIRE_MINUTE = 1L;



	public S3Service(@Value("${aws-property.s3-bucket-name}") final String bucketName, AWSConfig awsConfig) {
		this.bucketName = bucketName;
		this.awsConfig = awsConfig;
	}

	public String uploadImage(MultipartFile multipartFile, String folder) {
		final String key = folder + createFileName(multipartFile.getOriginalFilename());
		final S3Client s3Client = awsConfig.getS3Client();

		validateFileSize(multipartFile);

		PutObjectRequest request = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.contentType(multipartFile.getContentType())
			.contentLength(multipartFile.getSize())
			.contentDisposition("inline")
			.build();

		try {
			RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
			s3Client.putObject(request, requestBody);
			return key;
		} catch(IOException e) {
			throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
		}
	}


	public List<String> uploadImages(List<MultipartFile> multipartFileList, String folder) {
		final S3Client s3Client = awsConfig.getS3Client();
		List<String> list = new ArrayList<>();
		for (int i = 0; i < multipartFileList.size(); i++) {
			String key = folder + createFileName(multipartFileList.get(i).getOriginalFilename());
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(multipartFileList.get(i).getContentType())
				.contentLength(multipartFileList.get(i).getSize())
				.contentDisposition("inline")
				.build();

			try {
				RequestBody requestBody = RequestBody.fromBytes(multipartFileList.get(i).getBytes());
				s3Client.putObject(request, requestBody);
				list.add(key);
			} catch(IOException e) {
				throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
			}
		}
		return list;
	}

	// 파일명 (중복 방지)
	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	// 파일 유효성 검사
	private String getFileExtension(String fileName) {
		if (fileName.length() == 0) {
			throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
		}
		ArrayList<String> fileValidate = new ArrayList<>();
		fileValidate.add(".jpg");
		fileValidate.add(".jpeg");
		fileValidate.add(".png");
		fileValidate.add(".JPG");
		fileValidate.add(".JPEG");
		fileValidate.add(".PNG");
		System.out.println("여기여기기");
		String idxFileName = fileName.substring(fileName.lastIndexOf("."));
		if (!fileValidate.contains(idxFileName)) {
			throw new BadRequestException(Error.BAD_REQUEST_FILE_EXTENSION, Error.BAD_REQUEST_FILE_EXTENSION.getMessage());
		}
		System.out.println("여기기기기기ㅣ기");
		return fileName.substring(fileName.lastIndexOf("."));
	}

	// 이미지 삭제
	public void deleteImage(String key) throws IOException {
		final S3Client s3Client = awsConfig.getS3Client();

		s3Client.deleteObject((DeleteObjectRequest.Builder builder) ->
			builder.bucket(bucketName)
				.key(key)
				.build()
		);
	}

	public void deleteImages(List<String> keys) throws IOException{
		final S3Client s3Client = awsConfig.getS3Client();
		for (String key : keys) {
			s3Client.deleteObject((DeleteObjectRequest.Builder builder) ->
				builder.bucket(bucketName)
					.key(key)
					.build()
			);
		}
	}

	private void validateFileSize(MultipartFile image) {
		if (image.getSize() > MAX_FILE_SIZE) {
			throw new BadRequestException(Error.BAD_REQUEST_FILE_SIZE, Error.BAD_REQUEST_FILE_SIZE.getMessage());
		}
	}

	// 만료시간 1분

	public PresignedUrlVO getUploadPreSignedUrl(final String filename, final String folder) {
		final String fileName = createFileName(filename);
		final String key = folder + fileName;
		System.out.println("하이");
		log.info(fileName);
		log.info(key);

		S3Presigner preSigner = awsConfig.getS3Presigner();

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.build();
		log.info("-----");
		PutObjectPresignRequest preSignedUrlRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(PRE_SIGNED_URL_EXPIRE_MINUTE))
			.putObjectRequest(putObjectRequest)
			.build();
		log.info("-----");
		String url = preSigner.presignPutObject(preSignedUrlRequest).url().toString();

		return PresignedUrlVO.of(fileName, url);
	}

	public String getURL(String keyName) {
		final S3Client s3Client = awsConfig.getS3Client();
		try {
			GetUrlRequest request = GetUrlRequest.builder()
				.bucket(bucketName)
				.key(keyName)
				.build();

			URL url = s3Client.utilities().getUrl(request);
			return ""+url;

		} catch (S3Exception e) {
			throw new CustomException(Error.UNPROCESSABLE_PRESIGNEDURL_EXCEPTION, Error.UNPROCESSABLE_PRESIGNEDURL_EXCEPTION.getMessage());
			// Exception을 뿌려준다!! 사진 제대로 안 올라갔으니까 다시 올려라 ...
		}
	}
}
