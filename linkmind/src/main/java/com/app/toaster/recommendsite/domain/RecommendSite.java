package com.app.toaster.recommendsite.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long siteId;

    private String siteTitle;

    @Column(columnDefinition = "TEXT")
    private String siteUrl;

    private String siteImg;

    @Enumerated(EnumType.STRING)
    private Topic siteSub;

    @Builder
    public RecommendSite(String siteTitle, String siteUrl, String siteImg, Topic siteSub) {
        this.siteTitle =siteTitle;
        this.siteUrl=siteUrl;
        this.siteImg = siteImg;
        this.siteSub =siteSub;
    }



}
