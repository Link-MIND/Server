package com.app.toaster.toast.controller.response;

import com.app.toaster.link.domain.Link;

public record WeekLinkDto(Long linkId, String linkTitle, String linkImg, String linkUrl) {
    public static WeekLinkDto of(Link link){
        return new WeekLinkDto(link.getId(), link.getTitle(), link.getThumbnailUrl(), link.getLinkUrl());
    }
}
