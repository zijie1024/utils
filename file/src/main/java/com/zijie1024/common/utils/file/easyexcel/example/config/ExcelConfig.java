package com.zijie1024.common.utils.file.easyexcel.example.config;

import com.zijie1024.common.utils.file.easyexcel.ExcelManager;
import com.zijie1024.common.utils.file.easyexcel.example.model.Book;
import com.zijie1024.common.utils.file.easyexcel.example.model.People;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 字节幺零二四
 * @date 2024-04-21 14:37
 * @description ExcelConfig
 */
@Configuration
public class ExcelConfig {
    @Bean
    public ExcelManager<Book> bookExcelManager() {
        return new ExcelManager<>() {
            @Override
            public boolean checkExcelRow(Book data) {
                return super.checkExcelRow(data);
            }
        };
    }

    @Bean
    public ExcelManager<People> peopleExcelManager() {
        return new ExcelManager<>() {
        };
    }
}
