package com.app.toaster.parse.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// import com.app.toaster.external.client.aws.S3Service;

import com.app.toaster.controller.response.parse.OgResponse;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.BadRequestException;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.external.client.aws.AWSConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParsingService {
	private final String BASIC_THUMBNAIL;

	public ParsingService(@Value("${static-image.url}") final String basicThumbnail) {
		this.BASIC_THUMBNAIL = basicThumbnail;
	}

	public OgResponse getOg(String linkUrl) throws IOException {
		try {
			String title = getTitle(linkUrl);
			log.info(title);
			String image = getImage(linkUrl);
			log.info(image);
			return OgResponse.of(
				title == null || title.isBlank() ? "기본 토스트 제목" : title,
				image == null || image.isBlank() ? BASIC_THUMBNAIL : image
			);
		}catch (HttpStatusException | SSLHandshakeException e){
			return OgResponse.of("15자 내로 제목을 지어주세요.", BASIC_THUMBNAIL);
		}catch (ConnectException e){
			throw new BadRequestException(Error.BAD_REQUEST_URL, Error.BAD_REQUEST_URL.getMessage());
		}
	}
	// public String getOg(String linkUrl) throws IOException {
	// 	String image = getImage(linkUrl);
	// 	return image == null || image.isBlank() ? BASIC_THUMBNAIL : image;
	// }

	private String getTitle(String linkUrl) throws IOException {
		try {
			Document doc = Jsoup.connect(linkUrl).get();
			Elements ogTitleElements = doc.select("meta[property=og:title]");
			Elements titleElements = doc.select("head").select("title");
			if (ogTitleElements.isEmpty() && titleElements.isEmpty()) {
				return null;
			}
			return ogTitleElements.isEmpty()?titleElements.get(0).text(): ogTitleElements.get(0).attr("content");
		}catch (org.jsoup.HttpStatusException e){
			return null;
		}

	}

	private String getImage(String linkUrl){
		try {
			Document doc = Jsoup.connect(linkUrl).get();
			Elements iframes = doc.select("iframe");
			Elements ogBlogImage = new Elements();
			if (!iframes.isEmpty()){
				Document iframeDoc = Jsoup.parse(doc.select("iframe").get(0).html());
				ogBlogImage = iframeDoc.select("meta[property=og:image]");
			}
			Elements ogImageElements = doc.select("meta[property=og:image]");
			Elements ogImage = doc.select("img[property=src]");
			//짜증나게 iframe 안에 박아놓은 경우.
			return 	findImageAnywhere(ogImageElements, ogImage, ogBlogImage);
		}catch (MalformedURLException e){
			throw new CustomException(Error.MALFORMED_URL_EXEPTION,Error.MALFORMED_URL_EXEPTION.getMessage());
		}catch (org.jsoup.HttpStatusException e){
			return null;
		}catch (IOException e){
			throw new CustomException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
		}
	}

	private String findImageAnywhere(Elements metaCase, Elements imgCase, Elements iframeCase){
		if (!metaCase.isEmpty()){
			return metaCase.get(0).attr("content");
		}
		else if(!imgCase.isEmpty()){
			return metaCase.get(0).text();
		}
		else if(!iframeCase.isEmpty()){
			return iframeCase.get(0).attr("content");
		}
		else{
			return null;
		}
	}

}
