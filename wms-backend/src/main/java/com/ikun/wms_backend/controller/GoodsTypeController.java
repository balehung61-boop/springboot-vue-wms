package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.controller.dto.QueryPageParam;
import com.ikun.wms_backend.entity.GoodsType;
import com.ikun.wms_backend.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goodstype")
public class GoodsTypeController {

    @Autowired
    private GoodsTypeService goodsTypeService;

    // 新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody GoodsType goodsType){
        return goodsTypeService.saveOrUpdate(goodsType) ? Result.success() : Result.fail();
    }

    // 删除
    @GetMapping("/delete")
    public Result delete(Integer id){
        return goodsTypeService.removeById(id) ? Result.success() : Result.fail();
    }

    // 查询所有 (用于下拉框选择)
    @GetMapping("/list")
    public Result list(){
        List<GoodsType> list = goodsTypeService.list();
        return Result.success(list);
    }

    // 分页查询 + 模糊搜索
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        Page<GoodsType> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GoodsType> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        String name = (String)query.getParam().get("name");
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(GoodsType::getName, name);
        }

        IPage result = goodsTypeService.page(page, lambdaQueryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }
}