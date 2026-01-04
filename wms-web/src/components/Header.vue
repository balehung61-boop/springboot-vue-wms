<template>
  <div style="display: flex; line-height: 60px;">
    <div style="margin-top: 8px;">
      <el-icon style="font-size: 20px; cursor: pointer" @click="collapse"><Fold /></el-icon>
    </div>
    <div style="flex: 1; text-align: center; font-size: 30px;">
      <span>欢迎来到仓库管理系统</span>
    </div>

    <!-- 动态显示当前登录用户的名字 -->
    <span style="font-size: 15px; margin-right: 10px;">{{ user.name }}</span>

    <el-dropdown>
      <el-icon style="font-size: 20px; margin-top: 20px; margin-right: 20px; cursor: pointer"><Setting /></el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="toPerson">个人中心</el-dropdown-item>
          <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 引入路由

const router = useRouter();
const user = ref({ name: '请先登录' });

onMounted(() => {
  // 从浏览器缓存中取出用户信息
  let userStr = sessionStorage.getItem("user");
  if(userStr){
    user.value = JSON.parse(userStr);
  }
})

// 退出登录逻辑
const logout = () => {
  // 1. 清空缓存
  sessionStorage.removeItem("user");
  // 2. 跳转回登录页
  router.push("/login");
  alert("退出成功");
}

// 个人中心逻辑
const toPerson = () => {
  // 这里为了简单，先弹窗显示。
  // 如果你想做得更高级，可以 router.push('/Person') 跳转到一个新页面
  alert(`
    账号: ${user.value.no}
    姓名: ${user.value.name}
    电话: ${user.value.phone}
    角色: ${user.value.roleId === 0 ? '超级管理员' : '普通用户'}
  `);
}

const collapse = () => {
  // 折叠菜单逻辑先留空，以后可以做
}
</script>

<style scoped>
</style>