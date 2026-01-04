<template>
  <el-container style="height: 100%; border: 1px solid #eee">

    <!-- 1. 左侧边栏 -->
    <el-aside width="200px" style="height: 100vh; background-color: #545c64; overflow-x: hidden;">
      <Aside></Aside>
    </el-aside>

    <el-container>
      <!-- 2. 顶部 Header -->
      <el-header style="text-align: right; font-size: 12px; border-bottom: 1px solid #ccc; line-height: 60px;">
        <Header></Header>
      </el-header>

      <!-- 3. 中间主要内容区域 -->
      <el-main>
        <!-- 关键：子路由出口，用户管理/仓库管理页面都会显示在这里 -->
        <router-view></router-view>
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup>
import Aside from "./Aside.vue";
import Header from "./Header.vue";
</script>

<style scoped>
.el-header {
  background-color: #fff;
  color: #333;
}
.el-aside {
  color: #333;
}
</style>