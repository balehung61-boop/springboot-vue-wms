package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.entity.Cart;
import com.ikun.wms_backend.entity.Goods;
import com.ikun.wms_backend.entity.Storage;
import com.ikun.wms_backend.service.CartService;
import com.ikun.wms_backend.service.GoodsService;
import com.ikun.wms_backend.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private StorageService storageService;

    // 加入购物车
    @PostMapping("/save")
    public Result save(@RequestBody Cart cart){
        return cartService.save(cart) ? Result.success() : Result.fail();
    }

    // 查询当前用户的购物车 (关联查询出商品名和仓库名)
    @GetMapping("/list")
    public Result list(@RequestParam Integer userId){
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> list = cartService.list(wrapper);

        // 遍历购物车，填入商品名字（这里简单处理，实际可用SQL连表）
        for(Cart c : list){
            Goods goods = goodsService.getById(c.getGoodsId());
            if(goods != null){
                c.setGoodsName(goods.getName());
                c.setPrice(goods.getPrice());
                Storage storage = storageService.getById(goods.getStorageId());
                if(storage != null) c.setStorageName(storage.getName());
            }
        }
        return Result.success(list);
    }

    // 移出购物车
    @GetMapping("/delete")
    public Result delete(Integer id){
        return cartService.removeById(id) ? Result.success() : Result.fail();
    }
}