package com.gbsw.board.service;

import com.gbsw.board.dto.report.AdminReportProcessRequest;
import com.gbsw.board.dto.report.AdminReportSummaryResponse;
import com.gbsw.board.dto.report.ReportRequest;
import com.gbsw.board.entity.Board;
import com.gbsw.board.entity.Comment;
import com.gbsw.board.entity.Report;
import com.gbsw.board.entity.User;
import com.gbsw.board.enums.ReportStatus;
import com.gbsw.board.enums.ReportType;
import com.gbsw.board.repository.BoardRepository;
import com.gbsw.board.repository.CommentRepository;
import com.gbsw.board.repository.ReportRepository;
import com.gbsw.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final DiscordNotifier discordNotifier;

    /// 유저 : 컨텐츠(게시글/댓글) 신고
    public void reportContent(ReportRequest dto) {
        /* 기본 신고 처리 */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setReason(dto.getReason());
        report.setStatus(ReportStatus.PENDING);

        if (dto.getBoardId() != null) {
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
            report.setBoard(board);
            report.setReportType(ReportType.BOARD);
        } else if (dto.getCommentId() != null) {
            Comment comment = commentRepository.findById(dto.getCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
            report.setComment(comment);
            report.setReportType(ReportType.COMMENT);
        } else {
            throw new IllegalArgumentException("신고 대상이 없음");
        }

        reportRepository.save(report);

        // 디스코드 알람 전송
        discordNotifier.sendUserReportNotification(username, ReportType.COMMENT.name(), dto.getReason());

        /* 해당 컨텐츠가 1시간 내 5회 이상 신고되었을 경우, 자동 삭제 처리. */
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Report> recentReports;

        boolean isBoardReport = dto.getBoardId() != null;
        boolean isCommentReport = dto.getCommentId() != null;

        if (isBoardReport) {
            recentReports = reportRepository.findRecentReportsForBoard(dto.getBoardId(), oneHourAgo);
            if (recentReports.size() >= 5) {
                Board board = boardRepository.findById(dto.getBoardId()).orElse(null);
                if (board != null) {
                    board.setDeleted(true);
                    boardRepository.save(board);

                    for (Report r : recentReports) {
                        r.setStatus(ReportStatus.RESOLVED);
                        r.setAdminComment("시스템에 의해 삭제된 컨텐츠입니다.");
                    }
                    reportRepository.saveAll(recentReports);

                    // 디스코드 알람 : 시스템 자동 삭제
                    discordNotifier.sendSystemDeleteNotification(dto.getBoardId(), ReportType.BOARD.name(), board.getContent());
                }
            }

        } else if (isCommentReport) {
            recentReports = reportRepository.findRecentReportsForComment(dto.getCommentId(), oneHourAgo);
            if (recentReports.size() >= 5) {
                Comment comment = commentRepository.findById(dto.getCommentId()).orElse(null);
                if (comment != null) {
                    comment.setDeleted(true);
                    commentRepository.save(comment);

                    for (Report r : recentReports) {
                        r.setStatus(ReportStatus.RESOLVED);
                        r.setAdminComment("시스템에 의해 삭제된 컨텐츠입니다.");
                    }
                    reportRepository.saveAll(recentReports);

                    // 디스코드 알람 : 시스템 자동 삭제
                    discordNotifier.sendSystemDeleteNotification(dto.getCommentId(), ReportType.COMMENT.name(), comment.getContent());
                }
            }
        }
    }


    /// 어드민 : 현재 신고된 모든 컨텐츠들을 조회
    public List<AdminReportSummaryResponse> getPendingSummaries() {
        List<Report> pendingReports = reportRepository.findByStatus(ReportStatus.PENDING);
        List<AdminReportSummaryResponse> summaries = new ArrayList<>();

        for (Report report : pendingReports) {
            Long contentId = (report.getReportType() == ReportType.BOARD)
                    ? report.getBoard().getId()
                    : report.getComment().getId();

            // 기존 List에 해당 컨텐츠로 summary가 생성되어 있는지 확인.
            AdminReportSummaryResponse match = null;
            for (AdminReportSummaryResponse summary : summaries) {
                if (summary.getContentId().equals(contentId) && summary.getContentType().equals(report.getReportType())) {
                    match = summary;
                    break;
                }
            }

            // 생성되어 있다면, reason만 추가한다.
            if (match != null) {
                match.getReasons().add(report.getReason());
            } else { // 그렇지 않다면, 새로 추가한다.
                AdminReportSummaryResponse newSummary =
                        new AdminReportSummaryResponse(
                                contentId,
                                report.getReportType(),
                                new ArrayList<>(List.of(report.getReason()))
                        );
                summaries.add(newSummary);
            }
        }
        return summaries;
    }


    /// 어드민 : 신고 처리
    @Transactional
    public void processReport(AdminReportProcessRequest request) {
        /* 해당 컨텐츠와 관련된 유저들의 모든 신고건 상태 처리 */
        List<Report> reports = null;
        if(request.getContentType().equals(ReportType.BOARD)) {
            reports = reportRepository.findByBoard_Id(request.getContentId());
        } else if(request.getContentType().equals(ReportType.COMMENT)) {
            reports = reportRepository.findByComment_Id(request.getContentId());
        }

        ReportStatus status = request.isApprove() ? ReportStatus.RESOLVED : ReportStatus.REJECTED;

        List<Report> updatedReports = reports.stream()
                .peek(r -> {
                    r.setStatus(status);
                    r.setAdminComment(request.getAdminComment());
                })
                .toList();
        reportRepository.saveAll(updatedReports);


        /* 실제 컨텐츠 삭제 처리 */
        if (request.isApprove()) {
            if (request.getContentType().equals(ReportType.BOARD)) {
                boardRepository.findById(request.getContentId()).ifPresent(board -> {
                    board.setDeleted(true);
                    boardRepository.save(board);
                });
            } else {
                commentRepository.findById(request.getContentId()).ifPresent(comment -> {
                    comment.setDeleted(true);
                    commentRepository.save(comment);
                });
            }
        }
    }
}

