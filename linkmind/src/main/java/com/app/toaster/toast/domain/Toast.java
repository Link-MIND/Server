package com.app.toaster.toast.domain;

import java.time.LocalDateTime;

import com.app.toaster.domain.Category;
import com.app.toaster.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Toast {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String linkUrl;

	private Boolean isRead;

	@Column(columnDefinition = "TEXT")
	private String thumbnailUrl;

	private LocalDateTime createdAt;

	private LocalDateTime updateAt;

	@Builder
	public Toast(User user, Category category, String title, String linkUrl, String thumbnailUrl) {
		this.category = category;
		this.user = user;
		this.title = title;
		this.linkUrl = linkUrl;
		this.isRead = false;
		this.createdAt = LocalDateTime.now();
		this.thumbnailUrl = thumbnailUrl;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public void updateCategory(Category category){ this.category = category;}

	public void updateThumbnail(String thumbnailUrl){
		this.thumbnailUrl = thumbnailUrl;
	}

	public void setUpdateAt(){
		this.updateAt=LocalDateTime.now();
	}

	public boolean isToastOwner(User presentUser){
		return this.user.equals(presentUser);
	}

}
