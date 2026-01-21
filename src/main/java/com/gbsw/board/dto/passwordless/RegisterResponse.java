package com.gbsw.board.dto.passwordless;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String qr;
    private String corpId;
    private String registerKey;
    private int terms;
    private String serverUrl;
    private String pushConnectorUrl;
    private String pushConnectorToken;
    private String userId;
}
