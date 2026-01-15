package com.gbsw.board.controller;

import com.gbsw.board.service.discord.DiscordNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final DiscordNotifier notifier;
    // TODO : 추후 어드민 기능이 필요할 때 사용
}
