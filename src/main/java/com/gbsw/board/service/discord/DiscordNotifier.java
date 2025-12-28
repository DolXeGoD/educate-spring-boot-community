package com.gbsw.board.service.discord;


import com.gbsw.board.dto.discord.DiscordEmbed;
import com.gbsw.board.dto.discord.DiscordEmbedField;
import com.gbsw.board.dto.discord.DiscordWebhookRequest;
import com.gbsw.board.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscordNotifier {

    private final DiscordFeignClient discordFeignClient;

    public void sendSystemDeleteNotification(Long contentId, String type, String content) {
        String truncatedContent = StringUtils.safeTruncate(content, 10);
        List<DiscordEmbedField> fields = List.of(
                DiscordEmbedField.builder().name("📌 유형").value(type).inline(true).build(),
                DiscordEmbedField.builder().name("🧑‍💻 ID").value(contentId.toString()).inline(true).build(),
                DiscordEmbedField.builder().name("📝 내용").value(truncatedContent).inline(false).build()
        );

        DiscordEmbed embed = DiscordEmbed.builder()
                .title("[시스템 자동 신고 처리 알림]")
                .description("아래 컨텐츠가 유저들의 신고로 자동 삭제 처리 되었습니다.")
                .color(0xFF0000) // 빨간색 (10진수)
                .timestamp(OffsetDateTime.now().toString())
                .fields(fields)
                .build();

        DiscordWebhookRequest payload = DiscordWebhookRequest.builder()
                .embeds(List.of(embed))
                .build();

        discordFeignClient.sendEmbed(payload);
    }

    public void sendUserReportNotification(String reporter, String type, String reason) {
        List<DiscordEmbedField> fields = List.of(
                DiscordEmbedField.builder().name("📌 유형").value(type).inline(true).build(),
                DiscordEmbedField.builder().name("🧑‍💻 신고자").value(reporter).inline(true).build(),
                DiscordEmbedField.builder().name("📝 사유").value(reason).inline(false).build()
        );

        DiscordEmbed embed = DiscordEmbed.builder()
                .title("🚨 신고 발생")
                .description("신고된 컨텐츠에 대한 정보입니다.")
                .color(0xFF0000) // 빨간색 (10진수)
                .timestamp(OffsetDateTime.now().toString())
                .fields(fields)
                .build();

        DiscordWebhookRequest payload = DiscordWebhookRequest.builder()
                .embeds(List.of(embed))
                .build();

        discordFeignClient.sendEmbed(payload);
    }
}

