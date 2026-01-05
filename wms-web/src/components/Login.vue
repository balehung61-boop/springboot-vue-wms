<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>

    <!-- 登录/注册卡片 -->
    <div class="login-card" :class="{ 'register-mode': isRegister }">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-icon">
          <el-icon :size="32"><Box /></el-icon>
        </div>
        <h1 class="logo-title">WMS</h1>
        <p class="logo-subtitle">智能仓储管理系统</p>
      </div>

      <!-- 切换标签 -->
      <div class="auth-tabs">
        <div class="tab" :class="{ active: !isRegister }" @click="switchMode(false)">登录</div>
        <div class="tab" :class="{ active: isRegister }" @click="switchMode(true)">注册</div>
        <div class="tab-indicator" :class="{ right: isRegister }"></div>
      </div>

      <!-- 登录表单 -->
      <el-form v-show="!isRegister" :model="loginForm" :rules="loginRules" ref="loginFormRef" class="auth-form">
        <el-form-item prop="no">
          <el-input v-model="loginForm.no" placeholder="请输入账号" size="large" class="modern-input">
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password 
            size="large"
            class="modern-input"
            @keyup.enter="login"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="rememberMe" class="remember-checkbox">记住密码</el-checkbox>
          <a href="#" class="forgot-link">忘记密码？</a>
        </div>

        <el-form-item>
          <el-button type="primary" class="submit-btn" size="large" @click="login" :loading="loading">
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-show="isRegister" :model="registerForm" :rules="registerRules" ref="registerFormRef" class="auth-form">
        <!-- 必填项 -->
        <div class="form-section">
          <div class="section-title">
            <el-icon><Star /></el-icon>
            基本信息（必填）
          </div>
          
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item prop="no">
                <el-input v-model="registerForm.no" placeholder="账号" size="large" class="modern-input">
                  <template #prefix>
                    <el-icon class="input-icon"><Stamp /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="name">
                <el-input v-model="registerForm.name" placeholder="姓名" size="large" class="modern-input">
                  <template #prefix>
                    <el-icon class="input-icon"><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="设置密码（至少6位）" 
              show-password 
              size="large"
              class="modern-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="确认密码" 
              show-password 
              size="large"
              class="modern-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Unlock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="phone">
            <el-input v-model="registerForm.phone" placeholder="手机号码" size="large" class="modern-input">
              <template #prefix>
                <el-icon class="input-icon"><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- 选填项 -->
        <div class="form-section optional">
          <div class="section-title optional-title" @click="showOptional = !showOptional">
            <el-icon><InfoFilled /></el-icon>
            更多信息（选填）
            <el-icon class="toggle-icon" :class="{ expanded: showOptional }"><ArrowDown /></el-icon>
          </div>
          
          <transition name="collapse">
            <div v-show="showOptional" class="optional-fields">
              <el-row :gutter="12">
                <el-col :span="12">
                  <el-form-item prop="age">
                    <el-input v-model.number="registerForm.age" placeholder="年龄" size="large" class="modern-input" type="number">
                      <template #prefix>
                        <el-icon class="input-icon"><Calendar /></el-icon>
                      </template>
                    </el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item prop="sex">
                    <el-select v-model="registerForm.sex" placeholder="性别" size="large" class="modern-select">
                      <el-option label="男" :value="1" />
                      <el-option label="女" :value="0" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item prop="email">
                <el-input v-model="registerForm.email" placeholder="电子邮箱" size="large" class="modern-input">
                  <template #prefix>
                    <el-icon class="input-icon"><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="description">
                <el-input 
                  v-model="registerForm.description" 
                  type="textarea" 
                  placeholder="个人简介（选填）" 
                  :rows="2"
                  class="modern-textarea"
                />
              </el-form-item>
            </div>
          </transition>
        </div>

        <el-form-item>
          <el-button type="primary" class="submit-btn register-btn" size="large" @click="register" :loading="registerLoading">
            <span v-if="!registerLoading">立即注册</span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部提示 -->
      <div class="auth-footer">
        <p v-if="!isRegister">还没有账号？<a href="#" @click.prevent="switchMode(true)">立即注册</a></p>
        <p v-else>已有账号？<a href="#" @click.prevent="switchMode(false)">去登录</a></p>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="copyright">
      © 2026 WMS 智能仓储管理系统 · 版权所有
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { User, Lock, Box, Phone, Message, Star, InfoFilled, ArrowDown, Stamp, Unlock, Calendar } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const loginFormRef = ref(null);
const registerFormRef = ref(null);
const loading = ref(false);
const registerLoading = ref(false);
const rememberMe = ref(false);
const isRegister = ref(false);
const showOptional = ref(false);

// 检查 URL 参数，支持从首页直接跳转到注册
onMounted(() => {
  if (route.query.mode === 'register') {
    isRegister.value = true;
  }
});

// 登录表单
const loginForm = reactive({
  no: '',
  password: ''
});

// 注册表单
const registerForm = reactive({
  no: '',
  name: '',
  password: '',
  confirmPassword: '',
  phone: '',
  age: null,
  sex: null,
  email: '',
  description: ''
});

// 登录验证规则
const loginRules = {
  no: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 手机号验证
const validatePhone = (rule, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号码'));
  } else {
    callback();
  }
};

// 邮箱验证
const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(); // 选填，可以为空
  } else {
    const emailReg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/;
    if (!emailReg.test(value)) {
      callback(new Error('请输入正确的邮箱格式'));
    } else {
      callback();
    }
  }
};

// 注册验证规则
const registerRules = {
  no: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
};

// 切换模式
const switchMode = (toRegister) => {
  isRegister.value = toRegister;
  // 清除验证状态
  if (toRegister) {
    loginFormRef.value?.clearValidate();
  } else {
    registerFormRef.value?.clearValidate();
  }
};

// 登录
const login = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      axios.post('http://localhost:8090/user/login', loginForm).then(res => {
        loading.value = false;
        if (res.data.code === 200) {
          sessionStorage.setItem('user', JSON.stringify(res.data.data));
          router.push('/Home');
          ElMessage.success({ message: '登录成功，欢迎回来！', duration: 2000 });
        } else {
          ElMessage.error({ message: '账号或密码错误，请重新输入', duration: 3000 });
        }
      }).catch(() => {
        loading.value = false;
        ElMessage.error('网络错误，请检查后端服务');
      });
    }
  });
};

// 注册
const register = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      registerLoading.value = true;
      // 提取需要发送的字段（不包括confirmPassword）
      const { confirmPassword, ...userData } = registerForm;
      
      axios.post('http://localhost:8090/user/register', userData).then(res => {
        registerLoading.value = false;
        if (res.data.code === 200) {
          ElMessage.success({ message: '注册成功！请登录', duration: 2000 });
          // 切换到登录模式并填充账号
          isRegister.value = false;
          loginForm.no = registerForm.no;
          loginForm.password = '';
          // 清空注册表单
          Object.keys(registerForm).forEach(key => {
            registerForm[key] = key === 'age' || key === 'sex' ? null : '';
          });
        } else {
          ElMessage.error({ message: res.data.msg || '注册失败，请稍后重试', duration: 3000 });
        }
      }).catch(() => {
        registerLoading.value = false;
        ElMessage.error('网络错误，请检查后端服务');
      });
    }
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 50%, #1e1e2f 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.2) 100%);
  top: -200px;
  right: -100px;
  animation: float1 20s ease-in-out infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.25) 0%, rgba(236, 72, 153, 0.15) 100%);
  bottom: -150px;
  left: -100px;
  animation: float2 25s ease-in-out infinite;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(16, 185, 129, 0.15) 100%);
  top: 40%;
  left: 10%;
  animation: float3 18s ease-in-out infinite;
}

.shape-4 {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.2) 0%, rgba(239, 68, 68, 0.15) 100%);
  top: 20%;
  right: 15%;
  animation: float4 22s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-50px, 50px) scale(1.1); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(60px, -40px) scale(1.05); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-30px, -30px) scale(1.15); }
}

@keyframes float4 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(40px, 30px) scale(1.1); }
}

/* 登录卡片 */
.login-card {
  width: 420px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 10;
  animation: cardFadeIn 0.6s ease-out;
  transition: all 0.3s ease;
}

.login-card.register-mode {
  width: 480px;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 24px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 10px 28px rgba(102, 126, 234, 0.4);
}

.logo-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtitle {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

/* 切换标签 */
.auth-tabs {
  display: flex;
  position: relative;
  background: #f3f4f6;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  position: relative;
  z-index: 1;
  transition: color 0.3s ease;
}

.tab.active {
  color: #fff;
}

.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.tab-indicator.right {
  transform: translateX(100%);
}

/* 表单样式 */
.auth-form {
  margin-top: 0;
}

.form-section {
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #667eea;
  margin-bottom: 12px;
}

.optional-title {
  cursor: pointer;
  color: #9ca3af;
}

.optional-title:hover {
  color: #667eea;
}

.toggle-icon {
  margin-left: auto;
  transition: transform 0.3s ease;
}

.toggle-icon.expanded {
  transform: rotate(180deg);
}

.modern-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 14px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.modern-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.12);
}

.modern-select {
  width: 100%;
}

.modern-select :deep(.el-select__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #e5e7eb;
}

.modern-textarea :deep(.el-textarea__inner) {
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.input-icon {
  color: #9ca3af;
  font-size: 16px;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-checkbox :deep(.el-checkbox__label) {
  color: #6b7280;
  font-size: 13px;
}

.forgot-link {
  color: #667eea;
  font-size: 13px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 46px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.45);
}

.register-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.35);
}

.register-btn:hover {
  box-shadow: 0 12px 32px rgba(16, 185, 129, 0.45);
}

/* 底部 */
.auth-footer {
  text-align: center;
  margin-top: 20px;
  color: #9ca3af;
  font-size: 13px;
}

.auth-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.auth-footer a:hover {
  text-decoration: underline;
}

/* 版权信息 */
.copyright {
  position: absolute;
  bottom: 24px;
  left: 0;
  right: 0;
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
}

/* 折叠动画 */
.collapse-enter-active,
.collapse-leave-active {
  transition: all 0.3s ease;
  max-height: 300px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  opacity: 0;
}

/* 自定义滚动条 */
.login-card::-webkit-scrollbar {
  width: 6px;
}

.login-card::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.login-card::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>