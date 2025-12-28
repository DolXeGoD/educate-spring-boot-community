package com.gbsw.board.controller;

import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.dto.report.AdminReportProcessRequest;
import com.gbsw.board.dto.report.AdminReportSummaryResponse;
import com.gbsw.board.dto.report.ReportRequest;
import com.gbsw.board.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    // 사용자 : 신고 제출
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> reportContent(@RequestBody ReportRequest dto) {
        reportService.reportContent(dto);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 어드민 : 처리 대기중인 신고 목록 조회
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminReportSummaryResponse>>> getPendingReports() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getPendingSummaries()));
    }

    // 어드민 : 신고 건 처리 (승인 or 거절)
    @PostMapping("/process")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> processReport(@RequestBody AdminReportProcessRequest request) {
        reportService.processReport(request);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

