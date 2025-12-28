package com.gbsw.board.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreate {
    @NotBlank(message = "제목은 필수항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수항목입니다.")
    private String content;
}