package com.app.toaster.external.client.discord;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class DiscordMessage {

    private String content;
    private List<Embed> embeds;

    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @ToString
    public static class Embed {

        private String title;
        private String description;
        private EmbedImage image;
    }
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @ToString
    public static class EmbedImage{

        private String url;
        @JsonProperty(value = "proxy_url")
        private String proxyUrl;

        private Integer height;
        private Integer width;
    }
}