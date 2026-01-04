<template>
  <div>
    <!-- 1. 顶部搜索栏 -->
    <div style="margin-bottom: 20px;">
      <el-input v-model="name" placeholder="请输入名字" style="width: 200px;" @keyup.enter="loadPost"></el-input>
      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="reset">重置</el-button>
      <el-button type="primary" @click="add">新增</el-button>
    </div>

    <!-- 2. 中间表格 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="no" label="账号" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="sex" label="性别" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.sex === 1 ? 'primary' : 'success'">
            {{ scope.row.sex === 1 ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleId" label="角色" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.roleId === 0 ? 'danger' : (scope.row.roleId === 1 ? 'primary' : 'success')">
            {{ scope.row.roleId === 0 ? '超级管理员' : (scope.row.roleId === 1 ? '管理员' : '用户') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="primary" @click="mod(scope.row)">编辑</el-button>

          <!-- 删除确认框 -->
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

    <!-- 4. 新增/编辑的弹窗 -->
    <el-dialog
        v-model="centerDialogVisible"
        title="用户信息"
        width="30%"
        center>

      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.no"></el-input>
        </el-form-item>
        <el-form-item label="名字">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <!-- 这里为了简单先用数字，以后可以改成下拉框 -->
          <el-radio-group v-model="form.roleId">
            <el-radio :label="0">超级管理员</el-radio>
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="2">普通用户</el-radio>
          </el-radio-group>
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
import { ref, onMounted, reactive, nextTick } from 'vue';
import axios from 'axios';

const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const name = ref('');
const centerDialogVisible = ref(false); // 控制弹窗显示

// 表单数据对象
const form = ref({
  id: '',
  no: '',
  name: '',
  password: '',
  age: '',
  phone: '',
  sex: '1',
  roleId: '2'
})

// 查询
const loadPost = () => {
  axios.post('http://localhost:8090/user/listPage', {
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

// 重置搜索
const reset = () => {
  name.value = '';
  loadPost();
}

// 点击新增按钮
const add = () => {
  centerDialogVisible.value = true;
  // 清空表单
  nextTick(()=>{
    form.value = {id:'', no:'', name:'', password:'', age:'', phone:'', sex:'1', roleId:'2'};
  })
}

// 点击保存 (新增或修改)
const save = () => {
  axios.post('http://localhost:8090/user/saveOrMod', form.value).then(res => {
    if(res.data.code === 200){
      alert('操作成功');
      centerDialogVisible.value = false; // 关闭弹窗
      loadPost(); // 刷新表格
    }else{
      alert('操作失败');
    }
  })
}

// 点击编辑
const mod = (row) => {
  centerDialogVisible.value = true;
  // 把当前行的数据填入表单
  // 注意：需要用深拷贝，否则修改表单会直接变表格
  nextTick(()=>{
    form.value = JSON.parse(JSON.stringify(row));
  })
}

// 点击删除
const del = (id) => {
  axios.get('http://localhost:8090/user/delete?id='+id).then(res => {
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