package com.zijie1024.common.utils.file.easyexcel.example.controller;

import com.zijie1024.common.utils.file.easyexcel.ExcelManager;
import com.zijie1024.common.utils.file.easyexcel.example.model.Book;
import com.zijie1024.common.utils.file.easyexcel.example.model.People;
import com.zijie1024.common.utils.file.easyexcel.example.service.ExampleService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 字节幺零二四
 * @date 2024-04-21 14:37
 * @description ExampleController
 */
@CrossOrigin
@RestController
@RequestMapping("util/file")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;
    private final ExcelManager<Book> bookExcelManager;
    private final ExcelManager<People> peopleExcelManager;

    @GetMapping("excel")
    public void download(HttpServletResponse resp) {
        bookExcelManager.write(
                exampleService.getDataList(),
                "abc",
                "123",
                resp
        );
    }

    @PostMapping("excel")
    public void upload(MultipartFile file) {
        bookExcelManager.save(
                file,
                exampleService::saveDataList
        );
    }
}
