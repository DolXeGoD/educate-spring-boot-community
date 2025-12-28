package com.gbsw.board.dto.discord;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscordEmbed {
    private String title;
    private String description;
    private int color; // 10진수 RGB
    private String timestamp;
    private List<DiscordEmbedField> fields;
}
