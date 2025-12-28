package com.gbsw.board.controller;

import com.gbsw.board.dto.comment.CommentCreate;
import com.gbsw.board.dto.comment.CommentResponse;
import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentCreate dto) {
        CommentResponse response = commentService.create(boardId, dto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreate dto) {
        CommentResponse response = commentService.update(commentId, dto);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }
}