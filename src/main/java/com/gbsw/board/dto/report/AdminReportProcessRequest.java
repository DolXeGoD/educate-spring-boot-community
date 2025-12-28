package com.gbsw.board.dto.report;

import com.gbsw.board.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReportProcessRequest {
    @NotNull(message = "컨텐츠 ID는 필수입니다.")
    private Long contentId;

    @NotNull(message = "컨텐츠 타입은 필수입니다.")
    private ReportType contentType;

    private boolean approve;
    private String adminComment;
}
