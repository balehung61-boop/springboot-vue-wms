package com.ikun.wms_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.controller.dto.QueryPageParam;
import com.ikun.wms_backend.entity.User;
import com.ikun.wms_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 查询所有 (用于测试)
    @GetMapping("/list")
    public Result list(){
        List<User> list = userService.list();
        return Result.success(list);
    }

    // 2. 新增
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return userService.save(user) ? Result.success() : Result.fail();
    }

    // 3. 更新
    @PostMapping("/mod")
    public Result mod(@RequestBody User user){
        return userService.updateById(user) ? Result.success() : Result.fail();
    }

    // 4. 新增或更新 (根据是否有ID判断)
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody User user){
        return userService.saveOrUpdate(user) ? Result.success() : Result.fail();
    }

    // 5. 删除
    @GetMapping("/delete")
    public Result delete(Integer id){
        return userService.removeById(id) ? Result.success() : Result.fail();
    }

    // 6. 分页查询 + 模糊搜索 (高级版，用对象接收参数)
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name"); // 从 map 里取参数

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(User::getName, name);
        }

        IPage result = userService.page(page, lambdaQueryWrapper);
        return Result.success(result.getRecords(), result.getTotal());
    }

    // 6. (简单版) 分页查询 + 搜索
    @GetMapping("/listP")
    public Result listP(@RequestParam(defaultValue = "1") int pageNum,
                        @RequestParam(defaultValue = "10") int pageSize,
                        @RequestParam(defaultValue = "") String name){

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 name 不为空，就像这样查询: where name like '%name%'
        if(StringUtils.isNotBlank(name)){
            lambdaQueryWrapper.like(User::getName, name);
        }

        IPage<User> res = userService.page(page, lambdaQueryWrapper);

        return Result.success(res.getRecords(), res.getTotal());
    }

    // ▼▼▼ 添加登录接口 ▼▼▼
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        List<User> list = userService.lambdaQuery()
                .eq(User::getNo, user.getNo())
                .eq(User::getPassword, user.getPassword()).list();

        if(list.size() > 0){
            return Result.success(list.get(0));
        }
        return Result.fail();
    }
    // ▲▲▲ 添加结束 ▲▲▲
}