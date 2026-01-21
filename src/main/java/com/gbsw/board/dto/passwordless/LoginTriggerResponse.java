package com.gbsw.board.dto.passwordless;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginTriggerResponse {
    private int term;
    private String pushConnectorUrl;
    private String pushConnectorToken;
    private String servicePassword;
    private String userId;
    private String sessionId;
}
