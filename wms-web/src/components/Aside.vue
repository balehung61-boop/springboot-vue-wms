<template>
  <el-menu
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      style="height: 100vh;"
      :default-active="activePath"
      router
  >
    <div style="height: 60px; line-height: 60px; text-align: center; color: #fff; font-weight: bold; font-size: 20px;">
      WMS 管理系统
    </div>

    <el-menu-item index="/Home">
      <el-icon><HomeFilled /></el-icon>
      <span>首页</span>
    </el-menu-item>

    <!-- ▼▼▼ 关键修改：只有 roleId 不是 2 (普通用户) 的人才能看到用户管理 ▼▼▼ -->
    <el-menu-item index="/User" v-if="user.roleId != 2">
      <el-icon><UserFilled /></el-icon>
      <span>用户管理</span>
    </el-menu-item>
    <!-- ▲▲▲ 修改结束 ▲▲▲ -->

    <el-menu-item index="/Storage">
      <el-icon><Box /></el-icon>
      <span>仓库管理</span>
    </el-menu-item>

    <el-menu-item index="/Goods">
      <el-icon><Goods /></el-icon>
      <span>物品管理</span>
    </el-menu-item>

    <el-menu-item index="/Cart" v-if="user.roleId == 2">
      <el-icon><ShoppingCart /></el-icon>
      <span>购物车</span>
    </el-menu-item>

    <el-menu-item index="/Order" v-if="user.roleId == 2">
      <el-icon><List /></el-icon>
      <span>订单管理</span>
    </el-menu-item>

    <el-menu-item index="/Person" v-if="user.roleId == 2">
      <el-icon><User /></el-icon>
      <span>个人信息</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

// ▼▼▼ 关键修改在这里 ▼▼▼
// 引入你要用到的所有图标组件
// HomeFilled, UserFilled 等是之前菜单用的
// ShoppingCart, List, User 是新加的普通用户菜单用的
import {
  HomeFilled,
  UserFilled,
  Box,
  Goods,
  ShoppingCart, // 购物车图标
  List,         // 订单图标
  User          // 个人信息图标
} from '@element-plus/icons-vue'
// ▲▲▲ 修改结束 ▲▲▲

const route = useRoute();
const activePath = computed(() => route.path);

// 获取当前登录用户
const user = ref(JSON.parse(sessionStorage.getItem('user')));
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>