<template>
  <div>
    <h3>我的历史订单</h3>
    <el-table :data="tableData" border>
      <!-- 这里字段要对应 Record 实体类 -->
      <el-table-column prop="goodsname" label="物品" />
      <el-table-column prop="count" label="数量" />
      <el-table-column prop="createtime" label="下单时间" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);

// 这里的 listPage 需要后端 RecordController 支持带 userId 的查询
// 简单做法：我们直接在前端过滤，或者复用 listPage
// 为了演示方便，这里假设你之前写的 listPage 可以用
onMounted(() => {
  // 这里你需要确保后端 listPage 接口如果收到 userId 参数，会加上过滤条件
  // 或者你可以专门写一个 /record/listByUser 接口
  // 此处仅为示例结构
  axios.post('http://localhost:8090/record/listPage', {
    pageNum: 1, pageSize: 100,
    param: { userId: user.id } // 需修改后端支持此参数
  }).then(res => {
    tableData.value = res.data.data;
  })
})
</script>