package com.gbsw.board.repository;

import com.gbsw.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Board> findByIsDeletedFalseOrderByViewCountDesc(Pageable pageable);

    Page<Board> findByIsDeletedFalseOrderByLikeCountDesc(Pageable pageable);

    @Query(value = "SELECT * FROM board WHERE MATCH(title) AGAINST(:keyword IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<Board> searchByTitle(@Param("keyword") String keyword);
}