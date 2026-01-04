<template>
  <el-menu
      class="modern-sidebar"
      text-color="rgba(255,255,255,0.85)"
      active-text-color="#fff"
      :default-active="activePath"
      router
  >
    <!-- Logo区域 -->
    <div class="logo-section">
      <div class="logo-icon">
        <el-icon :size="28"><Box /></el-icon>
      </div>
      <div class="logo-text">WMS</div>
      <div class="logo-subtitle">智能仓储管理</div>
    </div>

    <!-- 菜单项 -->
    <div class="menu-section">
      <el-menu-item index="/Home" class="menu-item">
        <el-icon><HomeFilled /></el-icon>
        <span>首页概览</span>
      </el-menu-item>

      <el-menu-item index="/User" v-if="user.roleId != 2" class="menu-item">
        <el-icon><UserFilled /></el-icon>
        <span>用户管理</span>
      </el-menu-item>

      <el-menu-item index="/Storage" class="menu-item">
        <el-icon><OfficeBuilding /></el-icon>
        <span>{{ user.roleId == 2 ? '仓库' : '仓库管理' }}</span>
      </el-menu-item>

      <el-menu-item index="/Goods" class="menu-item">
        <el-icon><Goods /></el-icon>
        <span>{{ user.roleId == 2 ? '商品' : '物品管理' }}</span>
      </el-menu-item>

      <el-menu-item index="/Cart" v-if="user.roleId == 2" class="menu-item">
        <el-icon><ShoppingCart /></el-icon>
        <span>购物车</span>
      </el-menu-item>

      <el-menu-item index="/Order" v-if="user.roleId == 2" class="menu-item">
        <el-icon><List /></el-icon>
        <span>订单管理</span>
      </el-menu-item>

      <el-menu-item index="/Person" v-if="user.roleId == 2" class="menu-item">
        <el-icon><User /></el-icon>
        <span>个人信息</span>
      </el-menu-item>
    </div>

    <!-- 底部装饰 -->
    <div class="sidebar-footer">
      <div class="version-tag">v2.0</div>
    </div>
  </el-menu>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeFilled,
  UserFilled,
  Box,
  Goods,
  ShoppingCart,
  List,
  User,
  OfficeBuilding
} from '@element-plus/icons-vue'

const route = useRoute();
const activePath = computed(() => route.path);
const user = ref(JSON.parse(sessionStorage.getItem('user')));
</script>

<style scoped>
.modern-sidebar {
  height: 100vh;
  border-right: none;
  background: linear-gradient(180deg, #1e1e2f 0%, #2d2d44 50%, #1e1e2f 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.modern-sidebar::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 20%, rgba(138, 43, 226, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(75, 0, 130, 0.08) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(-5%, 5%) rotate(5deg); }
}

/* Logo区域 */
.logo-section {
  padding: 24px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.08);
  position: relative;
}

.logo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
  transition: all 0.3s ease;
}

.logo-icon:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.45);
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #667eea 0%, #a855f7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtitle {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  margin-top: 4px;
  letter-spacing: 1px;
}

/* 菜单区域 */
.menu-section {
  padding: 16px 12px;
  position: relative;
  z-index: 1;
}

.menu-item {
  margin: 6px 0;
  border-radius: 12px;
  height: 48px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  transform: scaleY(0);
  transition: transform 0.3s ease;
  border-radius: 0 4px 4px 0;
}

.menu-item:hover {
  background: rgba(102, 126, 234, 0.15) !important;
  transform: translateX(4px);
}

.menu-item.is-active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.25) 0%, rgba(118, 75, 162, 0.25) 100%) !important;
}

.menu-item.is-active::before {
  transform: scaleY(1);
}

.menu-item :deep(.el-icon) {
  font-size: 18px;
  margin-right: 12px;
}

/* 底部 */
.sidebar-footer {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
}

.version-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  font-size: 11px;
  color: rgba(255,255,255,0.6);
  letter-spacing: 1px;
}
</style>