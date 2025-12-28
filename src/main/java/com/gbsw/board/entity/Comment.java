package com.gbsw.board.entity;

import com.gbsw.board.dto.comment.CommentResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CommentResponse toDto() {
        return CommentResponse.builder()
                .id(getId())
                .content(getContent())
                .authorName(getAuthor().getUsername())
                .parentId(getParent() != null ? getParent().getId() : null)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .isDeleted(isDeleted())
                .replies(new ArrayList<>()) // 초기값
                .build();
    }
}