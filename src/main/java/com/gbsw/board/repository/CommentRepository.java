package com.gbsw.board.repository;

import com.gbsw.board.entity.Board;
import com.gbsw.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글의 모든 댓글 조회 (삭제되지 않은 것만)
    List<Comment> findByBoardAndIsDeletedFalseOrderByCreatedAtAsc(Board board);

    // 특정 게시글의 부모 댓글만 조회 (1차 댓글)
    List<Comment> findByBoardAndParentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Board board);

    // 특정 부모 댓글의 대댓글 조회
    List<Comment> findByParentAndIsDeletedFalseOrderByCreatedAtAsc(Comment parent);

    // 특정 게시글의 댓글 수 조회
    int countByBoardAndIsDeletedFalse(Board board);
}

