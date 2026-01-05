package com.ikun.wms_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.wms_backend.entity.Record;
import com.ikun.wms_backend.entity.RecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    /**
     * 查询所有订单记录，关联商品信息（管理员使用）
     */
    @Select("SELECT r.id, r.goods_id as goodsId, r.user_id as userId, r.admin_id as adminId, " +
            "r.count, r.createtime, r.remark, " +
            "g.name as goodsName, g.price as goodsPrice, g.image as goodsImage, g.unit as goodsUnit, " +
            "(g.price * r.count) as totalPrice " +
            "FROM wms_record r " +
            "LEFT JOIN wms_goods g ON r.goods_id = g.id " +
            "ORDER BY r.id DESC")
    List<RecordVO> listAllOrders();
}