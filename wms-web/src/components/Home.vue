<template>
  <div>
    <!-- 1. 顶部用户信息区 -->
    <div style="margin-bottom: 20px;">
      <el-card shadow="hover">
        <div style="display: flex; align-items: center;">
          <!-- 头像 -->
          <el-avatar :size="60" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          <div style="margin-left: 20px;">
            <div style="font-size: 20px; font-weight: bold;">早安，管理员！祝你开始美好的一天。</div>
            <div style="margin-top: 5px; color: #999;">当前时间：{{ currentTime }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 2. 中间布局：左边轮播图，右边系统介绍 -->
    <el-row :gutter="20">

      <!-- 左侧：轮播图 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统展示</span>
            </div>
          </template>

          <el-carousel trigger="click" height="300px">
            <el-carousel-item v-for="item in 4" :key="item">
              <!-- 这里为了演示方便，用了CSS背景色，实际开发可以换成 <img src="..."> -->
              <div class="carousel-item-content" :class="'bg-' + item">
                <h3 class="small">WMS 智能仓储 {{ item }}</h3>
                <p>高效 · 智能 · 便捷</p>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </el-col>

      <!-- 右侧：系统介绍 -->
      <el-col :span="12">
        <el-card shadow="hover" style="height: 398px;">
          <template #header>
            <div class="card-header">
              <span>关于系统</span>
            </div>
          </template>

          <div style="line-height: 30px; font-size: 15px;">
            <p><b>项目名称：</b>Spring Boot + Vue3 仓库管理系统</p>
            <p><b>技术栈：</b></p>
            <ul>
              <li>后端：Spring Boot 3.x + MyBatis-Plus + MySQL 8.0</li>
              <li>前端：Vue 3 + Vite + Element Plus + Axios</li>
            </ul>
            <p><b>核心功能：</b></p>
            <el-tag type="success" style="margin-right: 5px;">用户管理</el-tag>
            <el-tag type="warning" style="margin-right: 5px;">仓库管理</el-tag>
            <el-tag type="primary" style="margin-right: 5px;">物品分类</el-tag>
            <el-tag type="danger" style="margin-right: 5px;">物品入库</el-tag>
            <el-tag type="info">物品出库</el-tag>

            <el-divider></el-divider>
            <p>这是一个适合初学者练手的全栈项目，涵盖了 Web 开发的核心流程。</p>
          </div>
        </el-card>
      </el-col>

    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

// 获取当前时间
const currentTime = ref('');
let timer = null;

const updateTime = () => {
  const date = new Date();
  currentTime.value = date.toLocaleString();
}

onMounted(() => {
  updateTime();
  // 每秒刷新一次时间
  timer = setInterval(updateTime, 1000);
})

onBeforeUnmount(() => {
  if(timer) clearInterval(timer);
})
</script>

<style scoped>
/* 轮播图样式 */
.carousel-item-content {
  color: #fff;
  text-align: center;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.carousel-item-content h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

/* 给轮播图加点渐变背景色，模拟图片效果 */
.bg-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.bg-2 { background: linear-gradient(135deg, #2af598 0%, #009efd 100%); }
.bg-3 { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%); }
.bg-4 { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); }
</style>