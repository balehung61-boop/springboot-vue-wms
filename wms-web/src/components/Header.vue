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
      <!-- 通知图标 + 弹窗 -->
      <el-popover
        v-model:visible="notificationVisible"
        placement="bottom-end"
        :width="360"
        popper-class="notification-popover"
        @show="fetchNotifications"
      >
        <template #reference>
          <div class="header-action notification-trigger" @click.stop="notificationVisible = !notificationVisible">
            <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" class="notification-badge">
              <el-icon :size="20"><Bell /></el-icon>
            </el-badge>
          </div>
        </template>
        
        <!-- 通知弹窗内容 -->
        <div class="notification-panel">
          <div class="notification-header">
            <span class="notification-title">消息通知</span>
            <el-button 
              v-if="unreadCount > 0"
              type="primary" 
              link 
              size="small"
              @click="handleMarkAllRead"
            >
              全部已读
            </el-button>
          </div>
          
          <div class="notification-list" v-loading="notificationLoading">
            <div v-if="notifications.length === 0" class="notification-empty">
              <el-icon :size="40" color="#c0c4cc"><ChatDotRound /></el-icon>
              <p>暂无消息通知</p>
            </div>
            
            <div 
              v-for="item in notifications" 
              :key="item.id"
              class="notification-item"
              :class="{ 'is-unread': item.isRead === 0 }"
              @click="handleNotificationClick(item)"
            >
              <div class="notification-icon" :class="'type-' + item.type">
                <el-icon v-if="item.type === 'system'"><InfoFilled /></el-icon>
                <el-icon v-else-if="item.type === 'order'"><DocumentChecked /></el-icon>
                <el-icon v-else-if="item.type === 'stock'"><WarningFilled /></el-icon>
                <el-icon v-else><Bell /></el-icon>
              </div>
              <div class="notification-content">
                <div class="notification-item-title">{{ item.title }}</div>
                <div class="notification-item-desc">{{ item.content }}</div>
                <div class="notification-item-time">{{ formatTime(item.createTime) }}</div>
              </div>
              <div v-if="item.isRead === 0" class="notification-dot"></div>
            </div>
          </div>
        </div>
      </el-popover>

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
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  HomeFilled, Search, Bell, User, Setting, SwitchButton, ArrowDown, Close,
  InfoFilled, DocumentChecked, WarningFilled, ChatDotRound 
} from '@element-plus/icons-vue';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const user = ref({ name: '请先登录', roleId: 2, id: null });
const searchKeyword = ref('');

// 通知相关状态
const notifications = ref([]);
const unreadCount = ref(0);
const notificationLoading = ref(false);
const notificationVisible = ref(false);
let refreshTimer = null;

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

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now - date;
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 1000)) + '分钟前';
  }
  // 小于24小时
  if (diff < 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 60 * 1000)) + '小时前';
  }
  // 小于7天
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前';
  }
  // 超过7天显示日期
  return date.toLocaleDateString('zh-CN');
};

// 获取未读通知数量
const fetchUnreadCount = async () => {
  if (!user.value.id) return;
  try {
    const res = await axios.get(`http://localhost:8090/notification/unread-count?userId=${user.value.id}`);
    if (res.data.code === 200) {
      unreadCount.value = res.data.data || 0;
    }
  } catch (error) {
    console.error('获取未读数量失败:', error);
  }
};

// 获取通知列表
const fetchNotifications = async () => {
  if (!user.value.id) return;
  notificationLoading.value = true;
  try {
    const res = await axios.get(`http://localhost:8090/notification/list?userId=${user.value.id}`);
    if (res.data.code === 200) {
      notifications.value = res.data.data || [];
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知失败');
  } finally {
    notificationLoading.value = false;
  }
};

// 点击通知标记为已读
const handleNotificationClick = async (item) => {
  if (item.isRead === 1) return;
  try {
    const res = await axios.post(`http://localhost:8090/notification/read/${item.id}`);
    if (res.data.code === 200) {
      item.isRead = 1;
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    }
  } catch (error) {
    console.error('标记已读失败:', error);
  }
};

// 全部标记为已读
const handleMarkAllRead = async () => {
  if (!user.value.id) return;
  try {
    const res = await axios.post(`http://localhost:8090/notification/read-all?userId=${user.value.id}`);
    if (res.data.code === 200) {
      notifications.value.forEach(item => item.isRead = 1);
      unreadCount.value = 0;
      ElMessage.success('已全部标记为已读');
    }
  } catch (error) {
    console.error('全部标记已读失败:', error);
    ElMessage.error('操作失败');
  }
};

onMounted(() => {
  let userStr = sessionStorage.getItem("user");
  if(userStr){
    user.value = JSON.parse(userStr);
    // 登录后立即获取未读数量
    fetchUnreadCount();
    // 每30秒刷新一次未读数量
    refreshTimer = setInterval(fetchUnreadCount, 30000);
  }
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
});

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

/* 通知弹窗样式 */
.notification-panel {
  margin: -12px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #9ca3af;
}

.notification-empty p {
  margin-top: 12px;
  font-size: 14px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border-bottom: 1px solid #f5f5f5;
}

.notification-item:hover {
  background: #f9fafb;
}

.notification-item.is-unread {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.notification-item.is-unread:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.notification-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}

.notification-icon.type-system {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.notification-icon.type-order {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.notification-icon.type-stock {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-item-title {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-item-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-item-time {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 6px;
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  flex-shrink: 0;
  margin-top: 6px;
}
</style>