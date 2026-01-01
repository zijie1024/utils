package com.zijie1024.common.utils.file.easyexcel.example.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 字节幺零二四
 * @date 2024-04-21 14:37
 * @description Book
 */
@Data
@AllArgsConstructor
@HeadStyle(fillForegroundColor = 9)
@HeadFontStyle(fontHeightInPoints = 11)
public class Book {
    @ExcelProperty("ID")
    private int id;
    @ExcelProperty("姓名")
    private String name;
}
