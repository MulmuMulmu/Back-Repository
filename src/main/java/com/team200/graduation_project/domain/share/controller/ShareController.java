package com.team200.graduation_project.domain.share.controller;

import com.team200.graduation_project.domain.share.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
}
