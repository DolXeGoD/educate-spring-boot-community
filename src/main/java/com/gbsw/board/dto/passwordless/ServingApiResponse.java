package com.gbsw.board.dto.passwordless;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServingApiResponse<T> {
    private boolean result;
    private String msg;
    private String code;
    private T data;

    public static <T> ServingApiResponse<T> error(String message) {
        return ServingApiResponse.<T>builder()
                .result(false)
                .code(null)
                .data(null)
                .msg(message)
                .build();
    }
}
