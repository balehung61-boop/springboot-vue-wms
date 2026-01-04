<template>
  <div class="modern-header">
    <!-- 左侧：面包屑/搜索 -->
    <div class="header-left">
      <div class="breadcrumb">
        <el-icon class="breadcrumb-icon"><HomeFilled /></el-icon>
        <span class="breadcrumb-text">{{ currentPage }}</span>
      </div>
    </div>

    <!-- 中间：搜索框 -->
    <div class="header-center">
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
    </div>

    <!-- 右侧：用户信息 -->
    <div class="header-right">
      <!-- 通知图标 -->
      <div class="header-action">
        <el-badge :value="3" :max="99" class="notification-badge">
          <el-icon :size="20"><Bell /></el-icon>
        </el-badge>
      </div>

      <!-- 用户信息 -->
      <el-dropdown trigger="click">
        <div class="user-profile">
          <el-avatar 
            :size="36" 
            :src="user.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'"
            class="user-avatar"
          />
          <div class="user-info">
            <div class="user-name">{{ user.name }}</div>
            <div class="user-role">{{ user.roleId === 0 ? '超级管理员' : user.roleId === 1 ? '管理员' : '用户' }}</div>
          </div>
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="modern-dropdown">
            <el-dropdown-item @click="toPerson">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item @click="toSettings">
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">
              <el-icon><SwitchButton /></el-icon>
              <span style="color: #f56c6c;">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { HomeFilled, Search, Bell, User, Setting, SwitchButton, ArrowDown, Close } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const user = ref({ name: '请先登录', roleId: 2 });
const searchKeyword = ref('');

// 页面名称映射
const pageNames = {
  '/Home': '首页概览',
  '/User': '用户管理',
  '/Storage': '仓库管理',
  '/Goods': '物品管理',
  '/Cart': '购物车',
  '/Order': '订单管理',
  '/Person': '个人信息'
};

const currentPage = computed(() => pageNames[route.path] || '首页');

onMounted(() => {
  let userStr = sessionStorage.getItem("user");
  if(userStr){
    user.value = JSON.parse(userStr);
  }
})

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  // 保存搜索关键词到 sessionStorage
  sessionStorage.setItem('globalSearch', searchKeyword.value);
  // 跳转到商品页面进行搜索
  router.push('/Goods');
  ElMessage.success(`正在搜索: ${searchKeyword.value}`);
}

const logout = () => {
  sessionStorage.removeItem("user");
  router.push("/login");
  ElMessage.success({ message: '已安全退出，期待您的下次访问！', duration: 2000 });
}

const toPerson = () => {
  router.push('/Person');
}

const toSettings = () => {
  ElMessage.info('系统设置功能开发中...');
}
</script>

<style scoped>
.modern-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

/* 左侧 */
.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-icon {
  font-size: 18px;
  color: #667eea;
}

.breadcrumb-text {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* 中间搜索框 */
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 40px;
}

.search-box {
  position: relative;
  width: 100%;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 16px;
}

.search-input {
  width: 100%;
  padding: 10px 40px 10px 42px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  background: #f9fafb;
  transition: all 0.3s ease;
  outline: none;
}

.search-input:focus {
  background: #fff;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.search-input::placeholder {
  color: #9ca3af;
}

.search-clear {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.search-clear:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.15);
}

/* 右侧 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-action {
  cursor: pointer;
  padding: 8px;
  border-radius: 10px;
  transition: all 0.3s ease;
  color: #6b7280;
}

.header-action:hover {
  background: #f3f4f6;
  color: #667eea;
}

.notification-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 用户信息 */
.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-profile:hover {
  background: #f3f4f6;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  text-align: left;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.3;
}

.user-role {
  font-size: 12px;
  color: #667eea;
}

.dropdown-arrow {
  color: #9ca3af;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.user-profile:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 下拉菜单 */
.modern-dropdown {
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12);
}

.modern-dropdown :deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: 8px;
  margin: 2px 0;
}

.modern-dropdown :deep(.el-dropdown-menu__item:hover) {
  background: #f3f4f6;
}
</style>