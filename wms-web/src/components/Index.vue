<template>
  <el-container class="main-container" direction="vertical">
    <!-- 顶部导航栏 -->
    <el-header class="top-header">
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
        <el-menu-item index="/Cart">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </el-menu-item>
        <el-menu-item index="/Order">
          <el-icon><Document /></el-icon>
          <span>订单</span>
        </el-menu-item>
        <!-- 管理员菜单 -->
        <el-menu-item v-if="user?.roleId === 1" index="/User">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 右侧区域 -->
      <div class="header-right">
        <!-- 搜索框 -->
        <div class="search-box">
          <el-icon class="search-icon"><Search /></el-icon>
          <input 
            type="text" 
            v-model="searchKeyword"
            placeholder="搜索商品、仓库..." 
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <span 
            v-if="searchKeyword" 
            class="search-clear"
            @click="searchKeyword = ''"
          >
            <el-icon><Close /></el-icon>
          </span>
        </div>

        <!-- 通知图标 -->
        <el-badge :value="3" class="notification-badge">
          <el-icon class="header-action" :size="20"><Bell /></el-icon>
        </el-badge>

        <!-- 用户下拉菜单 -->
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
import { ref, computed } from 'vue';
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
  Search, 
  Close, 
  Bell, 
  ArrowDown, 
  Setting, 
  SwitchButton 
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const user = JSON.parse(sessionStorage.getItem('user'));
const searchKeyword = ref('');

// 当前激活的菜单
const activeMenu = computed(() => route.path);

// 搜索处理
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  sessionStorage.setItem('globalSearch', searchKeyword.value.trim());
  router.push('/Goods');
  ElMessage.info(`正在搜索: ${searchKeyword.value}`);
};

// 退出登录
const logout = () => {
  sessionStorage.removeItem('user');
  router.push('/');
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
}

.nav-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: rgba(102, 126, 234, 0.3);
  color: #fff;
  border-bottom: 3px solid #667eea !important;
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

/* 搜索框 */
.search-box {
  position: relative;
  width: 240px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.5);
  font-size: 16px;
}

.search-input {
  width: 100%;
  padding: 8px 36px 8px 38px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  transition: all 0.3s ease;
  outline: none;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.search-input:focus {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(102, 126, 234, 0.6);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.search-clear {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.search-clear:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
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