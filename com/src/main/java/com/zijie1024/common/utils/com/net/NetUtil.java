package com.zijie1024.common.utils.com.net;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author 字节幺零二四
 * @date 2025-06-01 15:48
 * @description 提供网络、IP 解析等相关的通用方法
 */
public class NetUtil {

    private NetUtil() {
    }

    public static final String UNKNOWN_IP = "unknown";

    /**
     * 获取当前请求客户端真实的 IP 地址
     * 兼容反向代理（如 Nginx、CDN 等）场景下获取真实的远端客户端 IP。
     *
     * @return 客户端的真实 IP 地址字符串。如果不在 Web 上下文环境中则返回 "unknown"
     */
    public static String getIpAddress() {

        // 尝试获取当前线程绑定的请求上下文属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 如果当前线程不在标准的 Web 请求上下文中，则返回默认标识
        if (attributes == null) {
            return UNKNOWN_IP;
        }

        // 提取 HttpServletRequest 实例对象
        HttpServletRequest req = attributes.getRequest();

        // 优先尝试从 X-Forwarded-For 请求头中获取经过反向代理转发过来的原始 IP
        String ip = req.getHeader("X-Forwarded-For");

        // 如果各级代理未设置该请求头，或者获取到的值为 unknown，则降级获取直接相连的 TCP 远程地址
        if (ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }

        // 应对多级反向代理的情况，X-Forwarded-For 会存在多个 IP 并以逗号分隔，第一个即为客户端真实 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }
}
