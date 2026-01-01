package com.zijie1024.common.utils.mq.rabbit.example;

import com.zijie1024.common.utils.mq.rabbit.IRabbitManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 15:40
 * @description Sender
 */
@Service
@RequiredArgsConstructor
public class Sender {

    private final IRabbitManager rabbitManager;

    /**
     * 发送普通消息
     */
    public void sendMsg() {
        String id = UUID.randomUUID().toString().replace("-", "");
        String msg = "Normal Msg";
        rabbitManager.sendMsg(
                msg,
                id,
                Constant.EXCHANGE,
                Constant.ROUTING
        );
    }


    /**
     * 发送延时消息
     */
    public void sendDelayMsg() {
        String id = UUID.randomUUID().toString().replace("-", "");
        String msg = "Delay Msg";
        rabbitManager.sendMsg(
                msg,
                id,
                Constant.DELAY_MSG_EXCHANGE,
                Constant.DELAY_MSG_ROUTING,
                Constant.DELAY_MSG_DELAY_TIME,
                Constant.DELAY_MSG_RETRY_COUNT,
                Map.of(Constant.ARGS_NAME, "tom")
        );

    }
}
