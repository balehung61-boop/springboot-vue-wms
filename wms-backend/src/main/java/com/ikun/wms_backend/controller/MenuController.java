package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.entity.Menu;
import com.ikun.wms_backend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // 查询所有菜单
    // 访问路径: /menu/list?name=xxx
    @GetMapping("/list")
    public Result list(@RequestParam(name="name", required = false) String name){
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(Menu::getName, name);
        }
        List<Menu> list = menuService.list(lambdaQueryWrapper);
        return Result.success(list);
    }

    // 新增或修改
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Menu menu){
        return menuService.saveOrUpdate(menu) ? Result.success() : Result.fail();
    }

    // 删除
    @GetMapping("/delete")
    public Result delete(Integer id){
        return menuService.removeById(id) ? Result.success() : Result.fail();
    }
}