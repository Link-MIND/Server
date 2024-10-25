package com.app.toaster.timer.domain;

import com.app.toaster.domain.Category;
import com.app.toaster.domain.IntegerListConverter;
import com.app.toaster.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@OneToOne
	private Category category;

	private LocalTime remindTime;

	@Convert(converter = IntegerListConverter.class)
	private ArrayList<Integer> remindDates;

	private String comment;

	private Boolean isAlarm;

	private LocalDateTime updateAt = LocalDateTime.now();

	@Builder
	public Reminder(User user, Category category, String comment, LocalTime remindTime, ArrayList<Integer> remindDates, Boolean isAlarm) {
		this.user = user;
		this.category = category;
		this.comment = comment;
		this.remindDates = remindDates;
		this.remindTime = remindTime;
		this.isAlarm = isAlarm;
	}

	public void updateRemindTime(String remindTime){
		this.remindTime = LocalTime.parse(remindTime);
	}

	public void updateRemindDates(ArrayList<Integer> remindDates){
		this.remindDates = remindDates;
	}

	public void updateComment(String comment){
		this.comment = comment;
	}

	public void changeAlarm(){
		this.isAlarm = !isAlarm;
	}

	public void setUpdatedAtNow(){
		this.updateAt = LocalDateTime.now();
	}

}
