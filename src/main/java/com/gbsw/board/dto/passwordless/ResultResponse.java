package com.gbsw.board.dto.passwordless;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultResponse {
    private String auth;
    private String userId;
    private String hash;
}
