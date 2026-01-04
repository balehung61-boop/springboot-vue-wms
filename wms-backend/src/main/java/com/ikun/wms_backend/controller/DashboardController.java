package com.ikun.wms_backend.controller;

import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.entity.Goods;
import com.ikun.wms_backend.entity.Storage;
import com.ikun.wms_backend.service.GoodsService;
import com.ikun.wms_backend.service.RecordService;
import com.ikun.wms_backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RecordService recordService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public Result getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 仓库总数
        long storageCount = storageService.count();
        stats.put("storageCount", storageCount);

        // 2. 物品总数
        long goodsCount = goodsService.count();
        stats.put("goodsCount", goodsCount);

        // 3. 库存总量
        List<Goods> allGoods = goodsService.list();
        int totalStock = allGoods.stream()
                .mapToInt(g -> g.getCount() != null ? g.getCount() : 0)
                .sum();
        stats.put("totalStock", totalStock);

        // 4. 今日订单数 (count记录为负数的是出库/下单)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayOrders = recordService.lambdaQuery()
                .lt(com.ikun.wms_backend.entity.Record::getCount, 0)
                .ge(com.ikun.wms_backend.entity.Record::getCreatetime, todayStart)
                .count();
        stats.put("todayOrders", todayOrders);

        return Result.success(stats);
    }

    /**
     * 获取库存TOP5商品
     */
    @GetMapping("/topGoods")
    public Result getTopGoods() {
        List<Goods> topGoods = goodsService.lambdaQuery()
                .orderByDesc(Goods::getCount)
                .last("LIMIT 5")
                .list();
        return Result.success(topGoods);
    }

    /**
     * 获取所有仓库信息
     */
    @GetMapping("/storages")
    public Result getStorages() {
        List<Storage> storages = storageService.list();
        return Result.success(storages);
    }
}
