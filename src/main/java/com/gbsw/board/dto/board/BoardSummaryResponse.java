package com.gbsw.board.dto.board;

import com.gbsw.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardSummaryResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final boolean isDeleted;
    private final int viewCount;
    private final int likeCount;
    private final boolean likedByMe;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int commentCount;

    public BoardSummaryResponse(Board board, boolean likedByMe, int commentCount) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.author = board.getAuthor().getUsername();
        this.isDeleted = board.isDeleted();
        this.viewCount = board.getViewCount();
        this.likeCount = board.getLikeCount();
        this.likedByMe = likedByMe;
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        this.commentCount = commentCount;
    }
}