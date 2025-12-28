package com.gbsw.board.service;

import com.gbsw.board.dto.board.BoardCreate;
import com.gbsw.board.entity.Board;
import com.gbsw.board.entity.Like;
import com.gbsw.board.entity.User;
import com.gbsw.board.enums.AuthLevel;
import com.gbsw.board.exceptions.AuthenticationFailureException;
import com.gbsw.board.exceptions.AuthorizationFailureException;
import com.gbsw.board.exceptions.ResourceNotFoundException;
import com.gbsw.board.repository.BoardRepository;
import com.gbsw.board.repository.LikeRepository;
import com.gbsw.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.gbsw.board.enums.BoardSortType;

import java.util.List;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    // 전체 게시글 조회
    public Page<Board> findAll(BoardSortType sortType, Pageable pageable) {
        return switch (sortType) {
            case BoardSortType.LIKES -> boardRepository.findByIsDeletedFalseOrderByLikeCountDesc(pageable);
            case BoardSortType.VIEWS -> boardRepository.findByIsDeletedFalseOrderByViewCountDesc(pageable);
            case BoardSortType.RECENT -> boardRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        };
    }

    // 게시글 작성
    public Board create(BoardCreate dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("작성자 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .build();

        return boardRepository.save(board);
    }

    // 게시글 단건 조회
    public Board findById(Long id) {
        Board board = boardRepository.findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        board.setViewCount(board.getViewCount() + 1);
        return boardRepository.save(board);
    }

    // 게시글 수정
    public Board update(Long id, BoardCreate dto) {
        Board board = findById(id);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(AuthLevel.ADMIN.name()));
        // 권한 확인
        if (!board.getAuthor().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AuthorizationFailureException("수정 권한이 없습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        return boardRepository.save(board);
    }

    // 게시글 삭제
    public void delete(Long id) {
        Board board = findById(id);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(AuthLevel.ADMIN.name()));
        // 권한 확인
        if (!board.getAuthor().getUsername().equals(currentUsername) && !isAdmin) {
            throw new AuthenticationFailureException("삭제 권한이 없습니다.");
        }

        board.setDeleted(true);
        boardRepository.save(board);
    }

    // 게시물 좋아요/취소
    @Transactional
    public int toggleLike(Long boardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자 정보를 찾을 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        Optional<Like> existing = likeRepository.findByUserAndBoard(user, board);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get()); // 추천 취소

            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
        } else {
            Like like = Like.builder().user(user).board(board).build();
            likeRepository.save(like); // 추천 추가

            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);
        }

        return likeRepository.countByBoard(board);
    }

    public boolean isLikedByUser(Board board, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        String username = auth.getName();
        return likeRepository.findByUserAndBoard(
                userRepository.findByUsername(username).orElseThrow(), board
        ).isPresent();
    }

    // 게시글 검색 - 제목
    public List<Board> searchBoards(String keyword) {
        List<Board> searchResults = boardRepository.searchByTitle(keyword);
        return searchResults;
    }
}