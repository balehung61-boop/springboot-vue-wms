<template>
  <el-container class="main-container" direction="vertical">
    <!-- 顶部导航栏 -->
    <el-header class="top-header" :class="{ scrolled: isScrolled }">
      <!-- Logo区域 -->
      <div class="header-logo" @click="$router.push('/Home')">
        <div class="logo-icon">
          <el-icon :size="24"><Box /></el-icon>
        </div>
        <span class="logo-text">WMS</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        class="nav-menu"
        router
        :ellipsis="false"
      >
        <el-menu-item index="/Home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/Storage">
          <el-icon><OfficeBuilding /></el-icon>
          <span>仓库</span>
        </el-menu-item>
        <el-menu-item index="/Goods">
          <el-icon><Goods /></el-icon>
          <span>商品</span>
        </el-menu-item>
        <!-- 以下菜单需要登录才可访问 -->
        <template v-if="isLoggedIn">
          <!-- 购物车只对普通用户显示 -->
          <el-menu-item v-if="user?.roleId === 2" index="/Cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
          <el-menu-item index="/Order">
            <el-icon><Document /></el-icon>
            <span>订单</span>
          </el-menu-item>
          <!-- 管理员菜单 (仅超级管理员可见) -->
          <el-menu-item v-if="user?.roleId === 0" index="/User">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </template>
      </el-menu>

      <!-- 右侧区域 -->
      <div class="header-right">

        <!-- 已登录用户: 通知图标和下拉菜单 -->
        <template v-if="isLoggedIn">
          <el-badge :value="3" class="notification-badge">
            <el-icon class="header-action" :size="20"><Bell /></el-icon>
          </el-badge>

          <el-dropdown trigger="click" class="user-dropdown">
            <div class="user-info">
              <el-avatar :size="36" :src="user?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span class="user-name">{{ user?.name || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/Person')">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="toSettings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <!-- 游客: 登录/注册按钮 -->
        <template v-else>
          <button class="guest-btn login-btn" @click="$router.push('/login')">
            <el-icon><User /></el-icon>
            登录
          </button>
          <button class="guest-btn register-btn" @click="$router.push('/login?mode=register')">
            <el-icon><EditPen /></el-icon>
            注册
          </button>
        </template>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <!-- 装饰性背景 -->
      <div class="bg-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
      <div class="content-inner">
        <router-view></router-view>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  Box, 
  HomeFilled, 
  OfficeBuilding, 
  Goods, 
  ShoppingCart, 
  Document, 
  User, 
  Bell, 
  ArrowDown, 
  Setting, 
  SwitchButton,
  EditPen
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const user = ref(JSON.parse(sessionStorage.getItem('user')));
const isScrolled = ref(false);

// 检查用户是否已登录
const isLoggedIn = computed(() => !!user.value);

// 当前激活的菜单
const activeMenu = computed(() => route.path);

// 滚动检测
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  handleScroll(); // 初始化
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});


// 退出登录
const logout = () => {
  sessionStorage.removeItem('user');
  user.value = null; // 更新响应式状态
  router.push('/login');
  ElMessage.success('已安全退出');
};

// 设置（占位）
const toSettings = () => {
  ElMessage.info('系统设置功能开发中...');
};
</script>

<style scoped>
.main-container {
  min-height: 100vh;
  background: #f5f7fa;
}

/* 顶部导航栏 */
.top-header {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

/* Scrolled state - white background */
.top-header.scrolled {
  background: #ffffff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.top-header.scrolled .logo-text {
  color: #1e293b;
}

.top-header.scrolled .nav-menu :deep(.el-menu-item) {
  color: #475569;
}

.top-header.scrolled .nav-menu :deep(.el-menu-item:hover) {
  color: #1e293b;
  background: rgba(102, 126, 234, 0.08);
}

.top-header.scrolled .nav-menu :deep(.el-menu-item.is-active) {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.top-header.scrolled .header-action {
  color: #475569;
}

.top-header.scrolled .user-info {
  background: #f1f5f9;
}

.top-header.scrolled .user-name {
  color: #1e293b;
}

.top-header.scrolled .user-info .el-icon {
  color: #475569;
}

/* 游客登录/注册按钮 */
.guest-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  outline: none;
  flex-shrink: 0;
  white-space: nowrap;
}

.login-btn {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: rgba(255, 255, 255, 0.9);
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.6);
  color: #fff;
}

.register-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

/* Scrolled state - 游客按钮 */
.top-header.scrolled .login-btn {
  border-color: #667eea;
  color: #667eea;
}

.top-header.scrolled .login-btn:hover {
  background: rgba(102, 126, 234, 0.08);
  color: #5a6fd6;
}


/* Logo */
.header-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  margin-right: 40px;
  transition: opacity 0.3s ease;
}

.header-logo:hover {
  opacity: 0.85;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
  transition: color 0.3s ease;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  background: transparent;
  border: none;
  height: 64px;
}

.nav-menu :deep(.el-menu-item) {
  height: 64px;
  line-height: 64px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  padding: 0 20px;
  border-bottom: none !important;
  transition: all 0.3s ease;
  position: relative;
}

/* Animated underline on hover */
.nav-menu :deep(.el-menu-item::after) {
  content: '';
  position: absolute;
  bottom: 10px;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-menu :deep(.el-menu-item:hover::after) {
  width: 60%;
}

.nav-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: rgba(102, 126, 234, 0.3);
  color: #fff;
}

.nav-menu :deep(.el-menu-item.is-active::after) {
  width: 70%;
}

.nav-menu :deep(.el-menu-item .el-icon) {
  margin-right: 6px;
}

/* 右侧区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 通知图标 */
.notification-badge :deep(.el-badge__content) {
  top: 8px;
  right: 4px;
}

.header-action {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.header-action:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

/* 用户信息 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.15);
}

.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-info .el-icon {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

/* 主内容区域 */
.main-content {
  background: linear-gradient(180deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: calc(100vh - 64px);
  padding: 0;
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
}

.content-inner {
  position: relative;
  z-index: 1;
  width: 100%;
}

/* 装饰性背景 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.circle-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.15) 100%);
  top: -150px;
  right: -150px;
  animation: float1 20s ease-in-out infinite;
}

.circle-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, rgba(168, 85, 247, 0.2) 0%, rgba(236, 72, 153, 0.1) 100%);
  bottom: -100px;
  left: -100px;
  animation: float2 25s ease-in-out infinite;
}

.circle-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(16, 185, 129, 0.1) 100%);
  top: 40%;
  left: 30%;
  animation: float3 15s ease-in-out infinite;
}

@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-40px, 40px) scale(1.1); }
}

@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(50px, -30px) scale(1.05); }
}

@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-25px, -25px) scale(1.15); }
}

/* 覆盖 Element Plus 样式 */
.el-main {
  --el-main-padding: 0;
}

.el-header {
  --el-header-padding: 0;
  --el-header-height: 64px;
}
</style>