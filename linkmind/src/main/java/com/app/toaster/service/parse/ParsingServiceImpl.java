package com.app.toaster.service.parse;

import static org.springframework.web.util.UriUtils.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@Service
public class ParsingServiceImpl implements ParsingService{
	private final String BASIC_THUMBNAIL;

	public ParsingServiceImpl(@Value("${static-image.url}") final String basicThumbnail) {
		this.BASIC_THUMBNAIL = basicThumbnail;
	}

	@Override
	public OgResponse getOg(String linkUrl) throws IOException {
		try {
			String title = getTitle(linkUrl);
			String image = getImage(linkUrl);
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
		if (!metaCase.isEmpty()) {    //naver,brunch
			String excludeSlug = metaCase.get(0).attr("content");
			String responseUrl = extractSlugUrl(excludeSlug);
			return responseUrl == null ? excludeSlug : responseUrl;
		}
		else if(!imgCase.isEmpty()){
			return imgCase.get(0).text();
		}
		else if(!iframeCase.isEmpty()){
			return iframeCase.get(0).attr("content");
		}
		else{
			return null;
		}
	}

	private String extractSlugUrl(String slug) {
		// 정규표현식 패턴 설정
		Pattern pattern = Pattern.compile("(?<=fname=)([^&]+(?:%[0-9a-fA-F]{2})*)"); //fname=이라는게 앞에 나오면 그때부터 패턴 시작. &는 파라미터 구별하니 제외.
		// "/"같은거 16진수로 나오는 패턴이 %XX형태로 나오니 %이후에 16진수로 올수있는거 2자리가 *로 반복해서 올 수 있다를 명시.
		Matcher matcher = pattern.matcher(slug);

		// 매칭된 URL 추출
		if (matcher.find()) {
			// URL 디코딩 수행
			String url = matcher.group();
			url = decode(url, StandardCharsets.UTF_8);
			return url;
		}

		return null;
	}

}
