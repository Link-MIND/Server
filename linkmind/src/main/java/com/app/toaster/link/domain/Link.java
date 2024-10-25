package com.app.toaster.link.domain;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String linkUrl;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    private LocalDateTime updateAt;

    private boolean thisWeekLink = false;


    @Builder
    public Link(String title, String linkUrl, String thumbnailUrl) {
        this.title = title;
        this.linkUrl = linkUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setWeekLinkFalse(){
        this.thisWeekLink = false;
    }

    public void updateWeekLink(){
        this.updateAt = LocalDateTime.now();
        this.thisWeekLink =true;
    }
}
