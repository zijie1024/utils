package com.zijie1024.common.utils.mq.rabbit;

import com.zijie1024.common.utils.mq.json.JsonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-04-22 20:03
 * @description IRabbitUtil
 */
@Component
public class IRabbitHelper {

    private static RedisTemplate<String, String> redisTemplate;
    private static String MSG_KEY_FORMAT;
    private static IRabbitProperties properties;
    private static String MSG_CANCEL_FLAG_KEY_FORMAT;


    public IRabbitHelper(RedisTemplate<String, String> redisTemplate, IRabbitProperties properties) {
        IRabbitHelper.redisTemplate = redisTemplate;
        IRabbitHelper.properties = properties;
        // [消息ID]
        IRabbitHelper.MSG_KEY_FORMAT = properties.getRedis().getPrefix() + ":msg:%s";
        // [业务ID:消息ID]
        IRabbitHelper.MSG_CANCEL_FLAG_KEY_FORMAT = properties.getRedis().getPrefix() + ":header:cancel:%s:%s";
    }


    /**
     * 将消息关联缓存到 Redis
     *
     * @param id  消息ID
     * @param icd 消息关联对象
     */
    public static void setMsgCache(String id, ICorrelationData icd) {
        redisTemplate.opsForValue().set(
                geMsgRedisKey(id),
                JsonUtil.stringify(icd),
                properties.getRedis().getExpireTime(),
                TimeUnit.SECONDS
        );
    }

    /**
     * 从 Redis 取出消息关联对象
     *
     * @param id 消息ID
     * @return 关联对象
     */
    public static ICorrelationData getMsgCache(String id) {
        return JsonUtil.parse(redisTemplate.opsForValue().get(geMsgRedisKey(id)), ICorrelationData.class);
    }

    /**
     * 从 Redis 删除消息关联对象
     *
     * @param id 消息ID
     */
    public static void delMsgCache(String id) {
        redisTemplate.delete(geMsgRedisKey(id));
    }

    /**
     * 获取消息缓存 Key
     *
     * @param id 消息ID
     * @return 消息缓存 Key
     */
    public static String geMsgRedisKey(String id) {
        return MSG_KEY_FORMAT.formatted(id);
    }

    /**
     * 设置消息是否被取消的标志
     *
     * @param businessId    业务ID
     * @param correlationId 消息ID
     * @param flag          标志，即消息是否被取消
     */
    public static void setCancelFlag(Object businessId, String correlationId, boolean flag) {
        redisTemplate.opsForValue().set(MSG_CANCEL_FLAG_KEY_FORMAT.formatted(businessId, correlationId), String.valueOf(flag));
    }

    /**
     * 消息是否被取消
     *
     * @param msg 消息
     * @return true if flag is true or null  else false
     */
    public static boolean isCanceled(Message msg) {
        Object bid = msg.getMessageProperties().getHeaders().get(properties.getHeader().getBusinessIdKey());
        // 未设置businessId则默认不进行flag校验，即“永不取消”
        if (bid == null) return false;
        String key = MSG_CANCEL_FLAG_KEY_FORMAT.formatted(
                bid,
                msg.getMessageProperties().getCorrelationId()
        );
        String flag = redisTemplate.opsForValue().getAndDelete(key);
        // 已设置businessId且flag为null或true则表明消息已被取消
        return flag == null || Boolean.parseBoolean(flag);
    }

    /**
     * 取消消息
     *
     * @param businessId 业务ID
     */
    public static void cancelMsg(Object businessId) {
        // 删除flag（相当于将flag置为null）
        redisTemplate.delete(MSG_CANCEL_FLAG_KEY_FORMAT.formatted(businessId, ""));
    }


}
