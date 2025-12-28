package com.gbsw.board.repository;

import com.gbsw.board.entity.Report;
import com.gbsw.board.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByBoard_Id(Long id);
    List<Report> findByComment_Id(Long id);
    List<Report> findByStatus(ReportStatus status);

    @Query("SELECT r " +
            "FROM Report r " +
            "WHERE r.board.id = :boardId AND r.createdAt >= :since")
    List<Report> findRecentReportsForBoard(
            @Param("boardId") Long boardId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT r " +
            "FROM Report r " +
            "WHERE r.comment.id = :commentId AND r.createdAt >= :since")
    List<Report> findRecentReportsForComment(
            @Param("commentId") Long commentId,
            @Param("since") LocalDateTime since
    );


}
