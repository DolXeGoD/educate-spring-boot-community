package com.gbsw.board.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

// 댓글 작성 요청 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreate {
    private String content;

    @JsonProperty("parent_id")
    private Long parentId; // 대댓글인 경우 부모 댓글 ID
}