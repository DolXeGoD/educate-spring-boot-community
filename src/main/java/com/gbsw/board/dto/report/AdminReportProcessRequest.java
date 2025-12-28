package com.gbsw.board.dto.report;

import com.gbsw.board.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReportProcessRequest {
    private Long contentId;
    private ReportType contentType;
    private boolean approve;
    private String adminComment;
}
