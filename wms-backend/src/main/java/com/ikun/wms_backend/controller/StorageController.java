package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.controller.dto.QueryPageParam; // 确保引入了这个
import com.ikun.wms_backend.entity.Storage;
import com.ikun.wms_backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storage") // 确保这里是小写 storage
public class StorageController {

    @Autowired
    private StorageService storageService;

    // 新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Storage storage){
        return storageService.saveOrUpdate(storage) ? Result.success() : Result.fail();
    }

    // 删除
    @GetMapping("/delete")
    public Result delete(Integer id){
        return storageService.removeById(id) ? Result.success() : Result.fail();
    }

    // 分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        Page<Storage> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Storage> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        String name = (String)query.getParam().get("name");
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(Storage::getName, name);
        }

        IPage result = storageService.page(page, lambdaQueryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }

    // 查询所有 (专门给下拉框用的)
    @GetMapping("/list")
    public Result list(){
        List<Storage> list = storageService.list();
        return Result.success(list);
    }
}