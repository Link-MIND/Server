package com.app.toaster.service.timer;

import com.app.toaster.timer.controller.request.CreateTimerRequestDto;
import com.app.toaster.timer.controller.request.UpdateTimerCommentDto;
import com.app.toaster.timer.controller.request.UpdateTimerDateTimeDto;
import com.app.toaster.timer.controller.response.CompletedTimerDto;
import com.app.toaster.timer.controller.response.GetTimerPageResponseDto;
import com.app.toaster.timer.controller.response.GetTimerResponseDto;
import com.app.toaster.timer.controller.response.WaitingTimerDto;
import com.app.toaster.domain.Category;
import com.app.toaster.timer.domain.Reminder;
import com.app.toaster.domain.User;
import com.app.toaster.exception.Error;
import com.app.toaster.exception.model.CustomException;
import com.app.toaster.exception.model.ForbiddenException;
import com.app.toaster.exception.model.NotFoundException;
import com.app.toaster.exception.model.UnauthorizedException;
import com.app.toaster.infrastructure.CategoryRepository;
import com.app.toaster.timer.infrastructure.TimerRepository;
import com.app.toaster.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimerService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TimerRepository timerRepository;



    private final Locale locale = Locale.KOREA;

    private final int MaxTimerNumber = 5;

    @Transactional
    public void createTimer(Long userId, CreateTimerRequestDto createTimerRequestDto){
        User presentUser = findUser(userId);
        Category category = null;
        String comment = "전체";

        int timerNum = timerRepository.findAllByUser(presentUser).size();

        if(timerNum>=MaxTimerNumber){
            throw new CustomException(Error.BAD_REQUEST_CREATE_TIMER_EXCEPTION, Error.BAD_REQUEST_CREATE_TIMER_EXCEPTION.getMessage());
        }

        if(createTimerRequestDto.categoryId() != 0) {
            category = categoryRepository.findById(createTimerRequestDto.categoryId())
                    .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_CATEGORY_EXCEPTION, Error.NOT_FOUND_CATEGORY_EXCEPTION.getMessage()));
            comment = category.getTitle();
        }
        if(!timerRepository.findAllByCategoryAndUser(category,presentUser).isEmpty()){
            throw new CustomException(Error.UNPROCESSABLE_CREATE_TIMER_EXCEPTION, Error.UNPROCESSABLE_CREATE_TIMER_EXCEPTION.getMessage());
        }

        createTimerRequestDto.remindDates().sort(Comparator.naturalOrder());

        Reminder reminder = Reminder.builder()
                .user(presentUser)
                .category(category)
                .remindDates(createTimerRequestDto.remindDates())
                .remindTime(LocalTime.parse(createTimerRequestDto.remindTime()))
                .isAlarm(true)
                .comment(comment)
                .build();

        timerRepository.save(reminder);
    }
    @Transactional(readOnly = true)
    public GetTimerResponseDto getTimer(Long userId, Long timerId){
        Reminder reminder = timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_TIMER, Error.NOT_FOUND_TIMER.getMessage()));

        return GetTimerResponseDto.of(reminder);
    }

    @Transactional
    public void updateTimerDatetime(Long userId, Long timerId, UpdateTimerDateTimeDto updateTimerDateTimeDto){
        User presentUser = findUser(userId);

        Reminder reminder = timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_TIMER, Error.NOT_FOUND_TIMER.getMessage()));

        if (!presentUser.equals(reminder.getUser())){
            throw new CustomException(Error.INVALID_USER_ACCESS, Error.INVALID_USER_ACCESS.getMessage());
        }
        updateTimerDateTimeDto.remindDates().sort(Comparator.naturalOrder());
        reminder.updateRemindDates(updateTimerDateTimeDto.remindDates());
        reminder.updateRemindTime(updateTimerDateTimeDto.remindTime());

        reminder.setUpdatedAtNow();


    }

    @Transactional
    public void updateTimerComment(Long userId, Long timerId, UpdateTimerCommentDto updateTimerCommentDto){
        User presentUser = findUser(userId);

        Reminder reminder = timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_TIMER, Error.NOT_FOUND_TIMER.getMessage()));

        if (!presentUser.equals(reminder.getUser())){
            throw new UnauthorizedException(Error.INVALID_USER_ACCESS, Error.INVALID_USER_ACCESS.getMessage());
        }

        reminder.updateComment(updateTimerCommentDto.newComment());
        reminder.setUpdatedAtNow();

    }

    @Transactional
    public void changeAlarm(Long userId, Long timerId){
        User presentUser = findUser(userId);

        Reminder reminder = timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_TIMER, Error.NOT_FOUND_TIMER.getMessage()));

        if (!presentUser.equals(reminder.getUser())){
            throw new ForbiddenException(Error.INVALID_USER_ACCESS, Error.INVALID_USER_ACCESS.getMessage());
        }

        reminder.changeAlarm();
        reminder.setUpdatedAtNow();
    }


    @Transactional
    public void deleteTimer(Long userId, Long timerId){
        User presentUser = findUser(userId);

        Reminder reminder = timerRepository.findById(timerId)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_TIMER, Error.NOT_FOUND_TIMER.getMessage()));

        if (!presentUser.equals(reminder.getUser())){
            throw new UnauthorizedException(Error.INVALID_USER_ACCESS, Error.INVALID_USER_ACCESS.getMessage());
        }

        timerRepository.delete(reminder);
    }

    public GetTimerPageResponseDto getTimerPage(Long userId) {
        User presentUser = findUser(userId);
        ArrayList<Reminder> reminders = timerRepository.findAllByUser(presentUser);

        List<CompletedTimerDto> completedTimerList = reminders.stream()
                .filter(this::isCompletedTimer)
                .map(this::createCompletedTimerDto)
                .sorted(Comparator.comparing(CompletedTimerDto::remindTime))
                .collect(Collectors.toList());

        for(Reminder reminder : reminders){
            System.out.println(reminder.getId());
            System.out.println(isCompletedTimer(reminder));
        }

        List<WaitingTimerDto> waitingTimerList = reminders.stream()
                .filter(reminder -> !isCompletedTimer(reminder))
                .map(this::createWaitingTimerDto)
                .sorted(
                        Comparator.comparing((WaitingTimerDto dto) -> !dto.isAlarm())
                                .thenComparing(WaitingTimerDto::updateAt)
                )
                .collect(Collectors.toList());


        return GetTimerPageResponseDto.builder()
                .completedTimerList(completedTimerList)
                .waitingTimerList(waitingTimerList)
                .build();
    }

    //해당 유저 탐색
    private User findUser(Long userId){
        return userRepository.findByUserId(userId).orElseThrow(
                ()-> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage())
        );
    }

    // 완료된 타이머이고 알람이 켜져있는지 식별
    private boolean isCompletedTimer(Reminder reminder){
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime pastDateTime = now.minusHours(1);
        LocalDateTime futureDateTime = now.plusHours(1);

//        System.out.println(pastDateTime + ", " + now + ", " + futureDateTime);

        if (reminder.getRemindDates().contains(now.getDayOfWeek().getValue())) {
            LocalDateTime reminderDateTime = LocalDateTime.of(now.toLocalDate(), reminder.getRemindTime());

            return !reminderDateTime.isBefore(pastDateTime) && !reminderDateTime.isAfter(futureDateTime) && reminder.getIsAlarm();
        }

        return false;
    }

    // 완료된 타이머 날짜,시간 포맷
    private CompletedTimerDto createCompletedTimerDto(Reminder reminder) {
        String time = reminder.getRemindTime().format(DateTimeFormatter.ofPattern("a hh:mm",locale));
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("E요일",locale));
        return CompletedTimerDto.of(reminder, time, date);
    }

    // 대기중인 타이머 날짜,시간 포맷
    private WaitingTimerDto createWaitingTimerDto(Reminder reminder) {
        LocalDateTime now = LocalDateTime.now();
        String time = (reminder.getRemindTime().getMinute() == 0)
                ? reminder.getRemindTime().format(DateTimeFormatter.ofPattern("a h시",locale))
                : reminder.getRemindTime().format(DateTimeFormatter.ofPattern("a h시 mm분",locale));

        String dates = reminder.getRemindDates().stream()
                .map(this::mapIndexToDayString)
                .collect(Collectors.joining(", "));

        return WaitingTimerDto.of(reminder, time, dates);
    }

    // 인덱스로 요일 알아내기
    private String mapIndexToDayString(int index) {
        DayOfWeek dayOfWeek = DayOfWeek.of(index);
        String dayName = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, locale);

        return dayName.substring(0, 1);
    }

}
