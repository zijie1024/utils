package com.zijie1024.common.utils.file.easyexcel.example.service;

import com.zijie1024.common.utils.file.easyexcel.example.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 字节幺零二四
 * @date 2024-04-21 14:40
 * @description ExampleService
 */
@Service
public class ExampleService {
    /**
     * 获取数据列表
     */
    public List<Book> getDataList() {
        ArrayList<Book> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new Book(i, i + ""));
        }
        return list;
    }

    /**
     * 保存数据列表
     */
    public void saveDataList(List<Book> list) {
        for (Book it : list) {
            System.out.println(it);
        }
    }
}
