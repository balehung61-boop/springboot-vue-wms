package com.ikun.wms_backend.controller;

import com.ikun.wms_backend.common.Result;
import com.ikun.wms_backend.entity.Address;
import com.ikun.wms_backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 查询某人的所有地址
    @GetMapping("/list")
    public Result list(@RequestParam Integer userId) {
        List<Address> list = addressService.lambdaQuery().eq(Address::getUserId, userId).list();
        return Result.success(list);
    }

    // 新增或修改地址
    @PostMapping("/saveOrMod")
    public Result saveOrMod(@RequestBody Address address) {
        return addressService.saveOrUpdate(address) ? Result.success() : Result.fail();
    }

    // 删除地址
    @GetMapping("/delete")
    public Result delete(Integer id) {
        return addressService.removeById(id) ? Result.success() : Result.fail();
    }
}
