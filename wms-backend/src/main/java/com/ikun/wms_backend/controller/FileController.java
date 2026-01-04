package com.ikun.wms_backend.controller;

import com.ikun.wms_backend.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    // 1. 定义文件上传的本地存储路径 (请确保你的D盘有空间，或者改其他盘)
    // 注意：Windows路径结尾要加 \\
    private static final String FILE_PATH = "F:\\develop\\javaweb\\WMS\\wms-backend\\wms-backend\\wms-files\\";

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail();
        }

        // 2. 获取原文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 3. 生成唯一文件名 (防止文件名冲突)
        String newFileName = UUID.randomUUID() + suffix;

        // 4. 创建文件夹 (如果不存在)
        File dir = new File(FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 5. 保存文件到本地磁盘
        file.transferTo(new File(FILE_PATH + newFileName));

        // 6. 返回可访问的 URL 地址
        // 这里的 /image/ 是我们等下要在配置里映射的虚拟路径
        String url = "http://localhost:8090/image/" + newFileName;
        return Result.success(url);
    }
}