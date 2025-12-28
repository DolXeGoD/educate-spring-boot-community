package com.gbsw.board.dto.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gbsw.board.dto.comment.CommentResponse;
import com.gbsw.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardDetailResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    @JsonProperty("is_deleted")
    private final boolean isDeleted;
    @JsonProperty("view_count")
    private final int viewCount;
    @JsonProperty("like_count")
    private final int likeCount;
    @JsonProperty("liked_by_me")
    private final boolean likedByMe;
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private final LocalDateTime updatedAt;
    private final List<CommentResponse> comments;

    public BoardDetailResponse(Board board, boolean likedByMe, List<CommentResponse> comments) {
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
        this.comments = comments;
    }
}