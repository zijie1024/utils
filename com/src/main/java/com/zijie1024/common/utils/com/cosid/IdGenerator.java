package com.zijie1024.common.utils.com.cosid;

import lombok.Data;
import me.ahoo.cosid.snowflake.SnowflakeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


/**
 * @author 字节幺零二四
 * @date 2024-05-24 15:59
 * @description IdGenerator
 */
@Component
@Data
public class IdGenerator {

    private static SnowflakeId snowflakeId;

    @Lazy
    @Qualifier("__share__SnowflakeId")
    @Autowired
    private void setSnowflakeId(SnowflakeId snowflakeId) {
        IdGenerator.snowflakeId = snowflakeId;
    }

    /**
     * 生成分布式ID
     */
    public static Long id() {
        return snowflakeId.generate();
    }
}
