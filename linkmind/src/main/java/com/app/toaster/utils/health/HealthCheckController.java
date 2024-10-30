package com.app.toaster.utils.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.toaster.external.client.discord.DiscordMessageProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final DiscordMessageProvider discordMessageProvider;

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
