<template>
  <div>
    <!-- 1. 顶部搜索栏 -->
    <div style="margin-bottom: 20px;">
      <el-input v-model="name" placeholder="请输入仓库名" style="width: 200px;" @keyup.enter="loadPost"></el-input>
      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="reset">重置</el-button>
      <el-button type="primary" @click="add" v-if="user.roleId != 2">新增</el-button>
    </div>

    <!-- 2. 中间表格 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="仓库名" width="180" />
      <el-table-column prop="remark" label="备注" />

      <el-table-column label="操作" width="180" v-if="user.roleId != 2">
        <template #default="scope">
          <el-button size="small" type="primary" @click="mod(scope.row)">编辑</el-button>
          <el-popconfirm title="确定删除吗？" @confirm="del(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 3. 底部分页 -->
    <div style="padding: 10px 0;">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 4. 弹窗 -->
    <el-dialog
        v-model="centerDialogVisible"
        title="仓库信息"
        width="30%"
        center>

      <el-form :model="form" label-width="80px">
        <el-form-item label="仓库名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';

const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const name = ref('');
const centerDialogVisible = ref(false);
const form = ref({
  id: '',
  name: '',
  remark: ''
})

const loadPost = () => {
  axios.post('http://localhost:8090/storage/listPage', {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: {
      name: name.value
    }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      total.value = res.data.total;
    } else {
      alert('获取数据失败');
    }
  })
}

const reset = () => {
  name.value = '';
  loadPost();
}

const add = () => {
  centerDialogVisible.value = true;
  nextTick(()=>{
    form.value = {id:'', name:'', remark:''};
  })
}

const save = () => {
  axios.post('http://localhost:8090/storage/saveOrMod', form.value).then(res => {
    if(res.data.code === 200){
      alert('操作成功');
      centerDialogVisible.value = false;
      loadPost();
    }else{
      alert('操作失败');
    }
  })
}

const mod = (row) => {
  centerDialogVisible.value = true;
  nextTick(()=>{
    form.value = JSON.parse(JSON.stringify(row));
  })
}

const del = (id) => {
  axios.get('http://localhost:8090/storage/delete?id='+id).then(res => {
    if(res.data.code === 200){
      alert('删除成功');
      loadPost();
    }else{
      alert('删除失败');
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadPost();
}
const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadPost();
}

onMounted(() => {
  loadPost();
})
</script>

<style scoped>
</style>