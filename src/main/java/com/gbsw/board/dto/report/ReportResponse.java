package com.gbsw.board.dto.report;

import com.gbsw.board.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReportResponse {
    private ReportType targetType; // "BOARD" or "COMMENT"
    private Long targetId;
    private String reportedBy;
    private String reason;
    private LocalDateTime createdAt;
}
