package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.controller.dto.QueryPageParam;
import com.ikun.wms_backend.entity.Record;
import com.ikun.wms_backend.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    // 新增记录 (实际业务中，新增记录的同时应该修改库存)
    @PostMapping("/save")
    public Result save(@RequestBody Record record) {
        return recordService.save(record) ? Result.success() : Result.fail();
    }

    // 记录通常不允许随意修改或删除，所以这里只留查询接口即可，
    // 但为了方便测试，我先把删除加上
    @GetMapping("/delete")
    public Result delete(Integer id) {
        return recordService.removeById(id) ? Result.success() : Result.fail();
    }

    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        Page<Record> page = new Page<>(query.getPageNum(), query.getPageSize());
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();

        // 根据用户ID过滤（核心：用户只能看到自己的记录）
        Object userId = query.getParam().get("userId");
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        // 如果想根据 remark 模糊查询
        String remark = (String) query.getParam().get("name");
        if (StringUtils.isNotBlank(remark)) {
            queryWrapper.like("remark", remark);
        }

        // 按时间倒序排列（最新的在最上面）
        queryWrapper.orderByDesc("id");

        IPage result = recordService.page(page, queryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }
}
