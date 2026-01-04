<template>
  <div class="login-container">
    <div class="login-box">
      <div style="text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 20px;">
        WMS 仓库管理系统
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="no">
          <el-input v-model="form.no" placeholder="请输入账号" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%;" size="large" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { User, Lock } from '@element-plus/icons-vue';
import axios from 'axios';
import { useRouter } from 'vue-router'; // 引入路由跳转

const router = useRouter();
const formRef = ref(null);

const form = reactive({
  no: '',
  password: ''
});

const rules = {
  no: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      axios.post('http://localhost:8090/user/login', form).then(res => {
        if (res.data.code === 200) {
          // 1. 存储用户信息到浏览器 Session (关掉浏览器就没了，比较安全)
          sessionStorage.setItem('user', JSON.stringify(res.data.data));

          // 2. 跳转到主页
          router.push('/Home');

          alert('登录成功');
        } else {
          alert('账号或密码错误');
        }
      });
    }
  });
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100%;
  background-color: #2d3a4b; /* 深色背景 */
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-box {
  width: 350px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0,0,0,0.3);
}
</style>