package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.controller.dto.QueryPageParam;
import com.ikun.wms_backend.entity.Goods;
import com.ikun.wms_backend.entity.Record;
import com.ikun.wms_backend.service.GoodsService;
import com.ikun.wms_backend.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RecordService recordService; // 注入记录服务

    // 新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Goods goods){
        return goodsService.saveOrUpdate(goods) ? Result.success() : Result.fail();
    }

    // 删除
    @GetMapping("/delete")
    public Result delete(Integer id){
        return goodsService.removeById(id) ? Result.success() : Result.fail();
    }

    // 分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        Page<Goods> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        String name = (String)query.getParam().get("name");
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(Goods::getName, name);
        }

        IPage result = goodsService.page(page, lambdaQueryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }

    // ▼▼▼ 新增：入库操作 ▼▼▼
    @PostMapping("/inStock")
    public Result inStock(@RequestBody Record record){
        // 1. 找商品
        Goods goods = goodsService.getById(record.getGoodsId());
        if(goods == null) return Result.fail();

        // 2. 更新库存
        int newCount = goods.getCount() + record.getCount();
        goods.setCount(newCount);
        goodsService.updateById(goods);

        // 3. 记录入库流水
        record.setCreatetime(LocalDateTime.now());
        record.setCount(record.getCount()); // 入库是正数
        // 实际开发中这里要从 Session 获取当前登录人 ID，这里先写死 1 (admin)
        record.setUserId(1);
        record.setRemark(record.getRemark());
        recordService.save(record);

        return Result.success();
    }

    // ▼▼▼ 新增：出库操作 ▼▼▼
    @PostMapping("/outStock")
    public Result outStock(@RequestBody Record record){
        // 1. 找商品
        Goods goods = goodsService.getById(record.getGoodsId());
        if(goods == null) return Result.fail();

        // 2. 检查库存够不够
        int newCount = goods.getCount() - record.getCount();
        if(newCount < 0) {
            // 库存不足，返回失败提示
            Result res = Result.fail();
            res.setMsg("库存不足，无法出库！");
            return res;
        }

        // 3. 更新库存
        goods.setCount(newCount);
        goodsService.updateById(goods);

        // 4. 记录出库流水
        record.setCreatetime(LocalDateTime.now());
        record.setCount(-record.getCount()); // 出库记录为负数，方便统计
        record.setUserId(1);
        recordService.save(record);

        return Result.success();
    }
}