package com.app.toaster.link.service;

import com.app.toaster.toast.controller.response.WeekLinkDto;
import com.app.toaster.link.domain.Link;
import com.app.toaster.link.infrastructure.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    @Transactional
    public List<WeekLinkDto> getWeekLinks(){

        List<Link> lists = linkRepository.findAllByThisWeekLinkIsTrue();

        long days = Duration.between(lists.get(0).getUpdateAt(), LocalDateTime.now()).toDays();

        if(days>7){
            lists.forEach(Link::setWeekLinkFalse);
            lists = linkRepository.findRandom3Links().stream()
                    .peek(Link::updateWeekLink)
                    .toList();
        }


        return lists.stream().map(WeekLinkDto::of).toList();
    }

}
