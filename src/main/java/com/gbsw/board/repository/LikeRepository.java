package com.gbsw.board.repository;

import com.gbsw.board.entity.Board;
import com.gbsw.board.entity.Like;
import com.gbsw.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndBoard(User user, Board board);
    int countByBoard(Board board);
    void deleteByUserAndBoard(User user, Board board);
}