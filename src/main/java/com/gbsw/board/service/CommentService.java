package com.gbsw.board.service;

import com.gbsw.board.dto.comment.CommentCreate;
import com.gbsw.board.dto.comment.CommentResponse;
import com.gbsw.board.entity.Board;
import com.gbsw.board.entity.Comment;
import com.gbsw.board.entity.User;
import com.gbsw.board.enums.AuthLevel;
import com.gbsw.board.exceptions.AuthorizationFailureException;
import com.gbsw.board.exceptions.ResourceNotFoundException;
import com.gbsw.board.repository.BoardRepository;
import com.gbsw.board.repository.CommentRepository;
import com.gbsw.board.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    @Transactional
    public CommentResponse create(Long boardId, CommentCreate dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자 정보를 찾을 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("부모 댓글을 찾을 수 없습니다."));

            // 대댓글의 대댓글은 허용하지 않음
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("대댓글에는 댓글을 달 수 없습니다.");
            }
        }

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .board(board)
                .author(user)
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return savedComment.toDto();
    }

    // 게시글의 모든 댓글 조회 (계층형 구조로)
    public List<CommentResponse> findByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        List<Comment> parentComments = commentRepository.findByBoardAndParentIsNullAndIsDeletedFalseOrderByCreatedAtAsc(board);
        List<CommentResponse> responses = new ArrayList<>();

        for (Comment parent : parentComments) {
            CommentResponse parentResponse = parent.toDto();

            // 대댓글 조회
            List<Comment> replies = commentRepository.findByParentAndIsDeletedFalseOrderByCreatedAtAsc(parent);
            List<CommentResponse> replyResponses = new ArrayList<>();

            for (Comment reply : replies) {
                replyResponses.add(reply.toDto());
            }

            parentResponse.setReplies(replyResponses);
            responses.add(parentResponse);
        }

        return responses;
    }

    // 댓글 수정
    @Transactional
    public CommentResponse update(Long commentId, CommentCreate dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(AuthLevel.ADMIN.name())); // 또는 "ADMIN"

        if (!comment.getAuthor().getUsername().equals(username) && !isAdmin) {
            throw new AuthorizationFailureException("수정 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return updatedComment.toDto();
    }

    // 댓글 삭제 (논리 삭제)
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(AuthLevel.ADMIN.name())); // 또는 "ADMIN"

        if (!comment.getAuthor().getUsername().equals(username) && !isAdmin) {
            throw new AuthorizationFailureException("삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    // 게시글의 댓글 수 조회
    public int getCommentCount(Board board) {
        return commentRepository.countByBoardAndIsDeletedFalse(board);
    }
}
