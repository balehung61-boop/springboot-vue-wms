<template>
  <div>
    <h3>我的购物车</h3>

    <!-- 1. 表格区域 -->
    <el-table :data="tableData" border style="width: 100%" show-summary :summary-method="getSummaries">

      <el-table-column prop="goodsName" label="物品名称" width="180">
        <template #default="scope">
          <el-tag size="large">{{ scope.row.goodsName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="storageName" label="发货仓库" width="120" />

      <el-table-column prop="price" label="单价" width="120">
        <template #default="scope">
          <span style="color: red;">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="count" label="购买数量" width="150">
        <template #default="scope">
          <!-- 允许在购物车直接改数量，更像电商 -->
          <el-input-number v-model="scope.row.count" :min="1" size="small" @change="updateCount(scope.row)"></el-input-number>
        </template>
      </el-table-column>

      <el-table-column label="小计(总金额)" width="150">
        <template #default="scope">
          <!-- 自动计算：单价 * 数量 -->
          <span style="color: red; font-weight: bold;">
               ￥{{ (scope.row.price * scope.row.count).toFixed(2) }}
            </span>
        </template>
      </el-table-column>

      <el-table-column prop="createtime" label="添加时间" width="180">
        <template #default="scope">
          <el-icon><Clock /></el-icon>
          <span style="margin-left: 5px">{{ formatTime(scope.row.createtime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="success" size="small" @click="pay(scope.row)">结算(下单)</el-button>
          <el-button type="danger" size="small" @click="del(scope.row.id)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Clock } from '@element-plus/icons-vue'; // 记得引入图标

const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);

const load = () => {
  axios.get('http://localhost:8090/cart/list?userId=' + user.id).then(res => {
    tableData.value = res.data.data;
  })
}

// 格式化时间：把 2025-12-31T12:00:00 变成 2025-12-31 12:00
const formatTime = (timeStr) => {
  if(!timeStr) return '';
  return timeStr.replace('T', ' ').substring(0, 19);
}

// 结算
const pay = (row) => {
  axios.post('http://localhost:8090/goods/outStock', {
    goodsId: row.goodsId,
    count: row.count,
    remark: '用户下单购买',
    userId: user.id
  }).then(res => {
    if(res.data.code === 200){
      alert('下单成功！');
      del(row.id);
    } else {
      alert(res.data.msg);
    }
  })
}

// 移除
const del = (id) => {
  axios.get('http://localhost:8090/cart/delete?id='+id).then(res=>{
    load();
  })
}

// 更新数量 (可选功能：如果想把修改后的数量同步回数据库，需要后端加update接口)
// 这里暂时只做前端展示变化
const updateCount = (row) => {
  // 实际项目中这里应该调用 axios.post('/cart/update', row)
  console.log("数量变为：", row.count);
}

// 底部合计逻辑 (Element Plus 表格自带功能)
const getSummaries = (param) => {
  const { columns, data } = param;
  const sums = [];
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '总价合计';
      return;
    }
    // 只计算“小计”这一列
    if (column.label === '小计(总金额)') {
      const values = data.map(item => item.price * item.count);
      const total = values.reduce((prev, curr) => {
        const value = Number(curr);
        if (!isNaN(value)) {
          return prev + curr;
        } else {
          return prev;
        }
      }, 0);
      sums[index] = '￥' + total.toFixed(2);
    } else {
      sums[index] = '';
    }
  });
  return sums;
}

onMounted(() => { load(); })
</script>

<style scoped>
</style>