package com.gbsw.board.dto.discord;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscordEmbedField {
    private String name;
    private String value;
    private boolean inline;
}
