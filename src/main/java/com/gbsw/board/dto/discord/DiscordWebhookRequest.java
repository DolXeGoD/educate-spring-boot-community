package com.gbsw.board.dto.discord;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscordWebhookRequest {
    private List<DiscordEmbed> embeds;
}
