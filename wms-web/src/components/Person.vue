<template>
  <div style="padding: 20px;">
    <el-row :gutter="20">

      <!-- 左侧：个人名片区 -->
      <el-col :span="6">
        <el-card style="text-align: center;">
          <el-avatar :size="100" :src="form.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
          <div style="margin-top: 20px; font-size: 20px; font-weight: bold;">{{ form.name }}</div>
          <div style="margin-top: 10px; color: #999;">{{ form.roleId === 0 ? '超级管理员' : '普通用户' }}</div>
          <div style="margin-top: 20px;">
            <!-- 上传组件 -->
            <!-- action: 后端上传接口地址 -->
            <!-- show-file-list: 不显示上传列表，只显示结果 -->
            <!-- on-success: 上传成功后的回调 -->
            <el-upload
                action="http://localhost:8090/file/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
            >
              <el-button type="primary" plain>更换头像</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：详细信息与设置 -->
      <el-col :span="18">
        <el-card>
          <el-tabs v-model="activeTab">

            <!-- Tab 1: 基础信息 -->
            <el-tab-pane label="基础信息" name="info">
              <el-form :model="form" label-width="100px" style="max-width: 500px; margin-top: 20px;">
                <el-form-item label="账号">
                  <el-input v-model="form.no" disabled></el-input>
                </el-form-item>
                <el-form-item label="姓名">
                  <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="电话">
                  <el-input v-model="form.phone"></el-input>
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="form.email"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="form.sex">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="个人简介">
                  <el-input type="textarea" v-model="form.description" rows="3"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveUser">保存基础信息</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Tab 2: 安全设置 -->
            <el-tab-pane label="安全设置" name="security">
              <el-form label-width="100px" style="max-width: 500px; margin-top: 20px;">
                <el-form-item label="原密码">
                  <el-input type="password" placeholder="如果要修改密码请输入旧密码"></el-input>
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="form.password" type="password" show-password></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="danger" @click="saveUser">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Tab 3: 收货地址管理 -->
            <el-tab-pane label="收货地址" name="address">
              <div style="margin-bottom: 15px;">
                <el-button type="success" size="small" @click="addAddress">新增地址</el-button>
              </div>
              <el-table :data="addressList" border stripe>
                <el-table-column prop="linkman" label="联系人" width="120" />
                <el-table-column prop="phone" label="电话" width="120" />
                <el-table-column prop="address" label="详细地址" />
                <el-table-column label="默认" width="80">
                  <template #default="scope">
                    <el-tag v-if="scope.row.isDefault === 'Y'" type="success">默认</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="scope">
                    <el-button link type="primary" @click="editAddress(scope.row)">编辑</el-button>
                    <el-button link type="danger" @click="delAddress(scope.row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 地址弹窗 -->
    <el-dialog v-model="addrDialogVisible" title="地址信息" width="30%">
      <el-form :model="addrForm" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="addrForm.linkman"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="addrForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input type="textarea" v-model="addrForm.address"></el-input>
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addrForm.isDefault" active-value="Y" inactive-value="N"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addrDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

const user = JSON.parse(sessionStorage.getItem('user'));
const activeTab = ref('info');
const form = ref(user); // 用户信息表单

// --- 地址相关变量 ---
const addressList = ref([]);
const addrDialogVisible = ref(false);
const addrForm = ref({
  id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N'
});

// 1. 保存用户信息
const saveUser = () => {
  axios.post('http://localhost:8090/user/mod', form.value).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('保存成功，请重新登录生效');
      // 更新缓存
      sessionStorage.setItem('user', JSON.stringify(form.value));
    } else {
      ElMessage.error('保存失败');
    }
  });
}
const handleAvatarSuccess = (res) => {
  // res 就是后端返回的 Result 对象
  if (res.code === 200) {
    // 1. 获取后端返回的图片 URL (例如 http://localhost:8090/image/abc.jpg)
    form.value.avatar = res.data;

    // 2. 自动保存到数据库
    saveUser();
  } else {
    alert('头像上传失败');
  }
}


// --- 地址管理逻辑 ---

const loadAddress = () => {
  axios.get(`http://localhost:8090/address/list?userId=${user.id}`).then(res => {
    addressList.value = res.data.data;
  });
}

const addAddress = () => {
  addrDialogVisible.value = true;
  addrForm.value = { id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N' };
}

const editAddress = (row) => {
  addrDialogVisible.value = true;
  addrForm.value = JSON.parse(JSON.stringify(row));
}

const saveAddress = () => {
  axios.post('http://localhost:8090/address/saveOrMod', addrForm.value).then(res => {
    if(res.data.code === 200){
      ElMessage.success('地址保存成功');
      addrDialogVisible.value = false;
      loadAddress();
    }
  });
}

const delAddress = (id) => {
  axios.get(`http://localhost:8090/address/delete?id=${id}`).then(res => {
    if(res.data.code === 200){
      ElMessage.success('删除成功');
      loadAddress();
    }
  });
}

onMounted(() => {
  loadAddress();
})
</script>

<style scoped>
.el-card {
  margin-bottom: 20px;
}
</style>