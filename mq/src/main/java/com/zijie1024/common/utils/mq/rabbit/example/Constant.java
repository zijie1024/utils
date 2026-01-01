package com.zijie1024.common.utils.mq.rabbit.example;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 15:44
 * @description Constant
 */
public class Constant {


    public static final String ARGS_NAME = "name";

    public static final String EXCHANGE = "exchange";
    public static final String ROUTING = "routing";
    public static final String QUEUE = "queue";
    public static final long DELAY_TIME = 10 * 1000;


    public static final String DELAY_MSG_EXCHANGE = "delay:exchange";
    public static final String DELAY_MSG_ROUTING = "delay:routing";
    public static final String DELAY_MSG_QUEUE = "delay:queue";
    public static final long DELAY_MSG_DELAY_TIME = 10 * 1000;
    public static final int DELAY_MSG_RETRY_COUNT = 5;
}
