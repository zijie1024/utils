package com.zijie1024.common.utils.code.behavior.example;

import com.zijie1024.common.utils.code.behavior.manager.BehaviorCodeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 字节幺零二四
 * @date 2024-05-07 17:01
 * @description BehaviorController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("behavior")
public class BehaviorController {

    private final BehaviorCodeManager behaviorCodeManager;

    @GetMapping
    public void check() {
        behaviorCodeManager.check("", "");
    }
}
