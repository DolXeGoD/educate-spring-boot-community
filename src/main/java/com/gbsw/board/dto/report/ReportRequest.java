package com.gbsw.board.dto.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportRequest {
    private Long boardId; // 게시글 신고 시 사용
    private Long commentId; // 댓글 신고 시 사용

    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;
}
