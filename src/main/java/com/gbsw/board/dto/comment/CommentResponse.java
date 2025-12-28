package com.gbsw.board.dto.comment;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// 댓글 응답 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorName;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private List<CommentResponse> replies; // 대댓글 목록
}
