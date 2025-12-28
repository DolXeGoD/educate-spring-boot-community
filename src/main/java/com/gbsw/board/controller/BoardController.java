package com.gbsw.board.controller;

import com.gbsw.board.dto.board.BoardCreate;
import com.gbsw.board.dto.board.BoardDetailResponse;
import com.gbsw.board.dto.board.BoardLikeResponse;
import com.gbsw.board.dto.board.BoardSummaryResponse;
import com.gbsw.board.dto.comment.CommentResponse;
import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.entity.Board;
import com.gbsw.board.enums.BoardSortType;
import com.gbsw.board.service.BoardService;
import com.gbsw.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "게시물", description = "게시물 관련 API")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "전체 게시글 조회", description = "게시글 전체 조회 시 사용하는 API 입니다. 정렬 타입과 페이징 관련 파라미터들이 필수로 포함되어 있어야 합니다.")
    public ResponseEntity<ApiResponse<Page<BoardSummaryResponse>>> getAllBoards(
            Authentication auth,
            @RequestParam(defaultValue = "RECENT") BoardSortType sortType,
            Pageable pageable
    ) {

        Page<BoardSummaryResponse> boards = boardService.findAll(sortType, pageable)
                .map(board -> {
                    System.out.println(board.getAuthor().getUsername());  // 여기서 먼저 접근
                    return new BoardSummaryResponse(
                            board,
                            boardService.isLikedByUser(board, auth),
                            commentService.getCommentCount(board)
                    );
                });

        return ResponseEntity.ok(ApiResponse.ok(boards));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 단건 조회", description = "게시글 단건 조회 시 사용하는 API 입니다. 조회 할 게시글의 id가 PathVariable로 포함되어야 합니다.")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoard(@PathVariable Long id, Authentication auth) {
        Board board = boardService.findById(id);
        boolean likedByMe = boardService.isLikedByUser(board, auth);
        List<CommentResponse> comments = commentService.findByBoardId(board.getId());
        return ResponseEntity.ok(ApiResponse.ok(new BoardDetailResponse(board, likedByMe, comments)));
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "게시글 검색 시 사용하는 API 입니다. 현재는 기본적으로 제목 검색만 가능합니다.")
    public ResponseEntity<List<BoardSummaryResponse>> search(@RequestParam String keyword, Authentication auth) {
        List<BoardSummaryResponse> boards = boardService.searchBoards(keyword)
                .stream()
                .map(board -> new BoardSummaryResponse(
                        board,
                        boardService.isLikedByUser(board, auth),
                        commentService.getCommentCount(board)
                ))
                .toList();

        return ResponseEntity.ok(boards);
    }

    @PostMapping
    @Operation(summary = "게시글 생성", description = "게시글 신규 등록(생성) 시 사용하는 API 입니다.")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> createBoard(@RequestBody BoardCreate dto) {
        Board board = boardService.create(dto);
        BoardDetailResponse boardResponse = new BoardDetailResponse(
                board,
                false,
                null
        );

        return ResponseEntity.ok(ApiResponse.ok(boardResponse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "등록되어 있는 기존 게시글 수정 시 사용하는 API 입니다.")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardCreate dto,
            Authentication auth
    ) {
        Board board = boardService.update(id, dto);
        boolean likedByMe = boardService.isLikedByUser(board, auth);
        List<CommentResponse> comments = commentService.findByBoardId(board.getId());

        BoardDetailResponse boardResponse = new BoardDetailResponse(
                board,
                likedByMe,
                comments
        );

        return ResponseEntity.ok(ApiResponse.ok(boardResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제 시 사용하는 API 입니다.")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/like/{boardId}")
    public ResponseEntity<ApiResponse<BoardLikeResponse>> likeOrUnlike(@PathVariable Long boardId) {
        int updatedLikeCount = boardService.toggleLike(boardId);
        return ResponseEntity.ok(ApiResponse.ok(new BoardLikeResponse()));
    }
}