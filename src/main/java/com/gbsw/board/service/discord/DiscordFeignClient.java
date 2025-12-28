package com.gbsw.board.service.discord;

import com.gbsw.board.dto.discord.DiscordWebhookRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "discordClient", url = "${discord.webhook.url}")
public interface DiscordFeignClient {

    @PostMapping
    void sendEmbed(@RequestBody DiscordWebhookRequest request);
}

