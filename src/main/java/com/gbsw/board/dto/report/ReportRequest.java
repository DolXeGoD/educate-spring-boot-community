package com.gbsw.board.dto.report;

import lombok.Getter;

@Getter
public class ReportRequest {
    private Long boardId;   // 게시글 신고 시 사용
    private Long commentId; // 댓글 신고 시 사용
    private String reason;
}
