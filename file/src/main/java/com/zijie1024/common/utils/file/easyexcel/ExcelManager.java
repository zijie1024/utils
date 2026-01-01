package com.zijie1024.common.utils.file.easyexcel;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author 字节幺零二四
 * @date 2024-04-21 12:59
 * @description ExcelManagerUp
 */
@Slf4j
public class ExcelManager<T> implements ReadListener<T> {

    private final Class<T> clazz;
    private Consumer<List<T>> saveData;
    private static final int BATCH_COUNT = 100;
    private List<T> cacheList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @SuppressWarnings("unchecked")
    public ExcelManager() {
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public final void invoke(T data, AnalysisContext analysisContext) {
        if (!checkExcelRow(data)) {
            String msg = "Excel数据行校验未通过(data=" + data + ")";
            log.info(msg);
            throw new RuntimeException(msg);
        }
        cacheList.add(data);
        if (cacheList.size() >= BATCH_COUNT) {
            saveData.accept(cacheList);
            cacheList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public final void doAfterAllAnalysed(AnalysisContext context) {
        saveData.accept(cacheList);
        cacheList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    /**
     * 校验单条数据
     *
     * @param data 数据
     * @return true if pass else false
     */
    public boolean checkExcelRow(T data) {
        return true;
    }


    /**
     * 导入Excel
     *
     * @param file     Excel文件
     * @param saveData 保存数据的操作
     */
    public final void save(
            MultipartFile file,
            Consumer<List<T>> saveData
    ) {
        try {
            this.saveData = saveData;
            EasyExcel.read(file.getInputStream(), clazz, this).sheet().doRead();
        } catch (Exception e) {
            String msg = "Excel解析失败(fileName=" + file.getName() + ")";
            log.info(msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 导出Excel
     *
     * @param data     数据
     * @param fileName 文件名
     * @param sheet    工作表名
     * @param resp     响应
     */
    public final void write(
            List<T> data,
            String fileName,
            String sheet,
            HttpServletResponse resp
    ) {
        // 设置响应信息
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("access-control-expose-headers", "content-disposition");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("content-disposition", "attachment;filename*=" + URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8));
        // 将数据写入响应
        try {
            EasyExcel.write(resp.getOutputStream(), clazz).sheet(sheet).doWrite(data);
        } catch (IOException e) {
            String msg = "Excel export failed (fileName=" + fileName + ")";
            log.info(msg);
            throw new RuntimeException(msg);
        }
    }

}
