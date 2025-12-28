package com.gbsw.board.dto.report;

import com.gbsw.board.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AdminReportSummaryResponse {
    private Long contentId;
    private ReportType contentType; // "BOARD" or "COMMENT"
    private List<String> reasons;
}
