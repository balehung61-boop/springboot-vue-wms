<template>
  <div class="user-manage-page">
    <!-- 1. Hero Header -->
    <div class="user-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="page-title">用户管理中心</h1>
        <p class="page-subtitle">全局用户账户管理，安全高效</p>
      </div>
    </div>

    <!-- 2. Filter Bar -->
    <div class="filter-bar-wrapper">
      <div class="filter-bar">
        <div class="filter-left">
          <div class="filter-item search-item">
            <el-icon class="search-icon"><Search /></el-icon>
            <input 
              v-model="searchName" 
              placeholder="搜索用户姓名..." 
              class="custom-input"
              @keyup.enter="loadPost"
            />
          </div>
        </div>
        <div class="filter-right">
          <el-button type="primary" class="action-btn" @click="loadPost">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button class="action-btn" @click="reset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
          <el-button type="success" class="action-btn" @click="add">
            <el-icon><Plus /></el-icon> 新增用户
          </el-button>
        </div>
      </div>
    </div>

    <!-- 3. Main Content -->
    <div class="main-content">
      <!-- Stats Cards -->
      <div class="stats-row">
        <div class="stat-card">
          <el-icon><UserFilled /></el-icon>
          <div class="stat-info">
            <span class="stat-value">{{ total }}</span>
            <span class="stat-label">总用户数</span>
          </div>
        </div>
        <div class="stat-card admin">
          <el-icon><Avatar /></el-icon>
          <div class="stat-info">
            <span class="stat-value">{{ tableData.filter(u => u.roleId === 0 || u.roleId === 1).length }}</span>
            <span class="stat-label">管理员</span>
          </div>
        </div>
        <div class="stat-card user">
          <el-icon><User /></el-icon>
          <div class="stat-info">
            <span class="stat-value">{{ tableData.filter(u => u.roleId === 2).length }}</span>
            <span class="stat-label">普通用户</span>
          </div>
        </div>
      </div>

      <!-- User Table -->
      <div class="table-card">
        <el-table :data="tableData" stripe style="width: 100%" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="no" label="账号" width="120" />
          <el-table-column prop="name" label="姓名" width="120">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="32" :src="row.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sex" label="性别" width="80">
            <template #default="{ row }">
              <el-tag :type="row.sex === 1 ? '' : 'success'" effect="plain" size="small">
                {{ row.sex === 1 ? '男' : '女' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="roleId" label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="getRoleType(row.roleId)" effect="dark" round>
                {{ getRoleName(row.roleId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" width="140">
            <template #default="{ row }">
              <span class="masked-phone">{{ maskPhone(row.phone) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱">
            <template #default="{ row }">
              <span class="masked-email">{{ maskEmail(row.email) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <el-tooltip content="编辑用户信息" placement="top">
                  <el-button link type="primary" @click="mod(row)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-popconfirm 
                  title="确定将该用户密码重置为 123 吗？" 
                  confirm-button-text="确认重置"
                  cancel-button-text="取消"
                  @confirm="resetPassword(row.id)"
                >
                  <template #reference>
                    <el-button link type="warning">
                      <el-icon><Key /></el-icon> 重置密码
                    </el-button>
                  </template>
                </el-popconfirm>
                <el-popconfirm 
                  title="确定删除该用户吗？此操作不可逆！" 
                  confirm-button-text="确认删除"
                  cancel-button-text="取消"
                  confirm-button-type="danger"
                  @confirm="del(row.id)"
                >
                  <template #reference>
                    <el-button link type="danger">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- Pagination -->
        <div class="pagination-section">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :current-page="pageNum"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑用户' : '新增用户'" 
      width="480px" 
      class="modern-dialog" 
      align-center
    >
      <el-form :model="form" label-width="80px" class="modern-form">
        <el-form-item label="账号">
          <el-input v-model="form.no" :disabled="!!form.id" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="用户姓名" />
        </el-form-item>
        <el-form-item label="初始密码" v-if="!form.id">
          <el-input v-model="form.password" placeholder="默认密码" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="form.phone" placeholder="手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1" border>男</el-radio>
            <el-radio :label="0" border>女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="0" />
            <el-option label="管理员" :value="1" />
            <el-option label="普通用户" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { 
  Search, Refresh, Plus, Edit, Delete, Key, 
  UserFilled, Avatar, User 
} from '@element-plus/icons-vue';

const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchName = ref('');
const dialogVisible = ref(false);
const saving = ref(false);

const form = ref({
  id: '', no: '', name: '', password: '123', age: 25, phone: '', sex: 1, roleId: 2
});

// Role helpers
const getRoleName = (roleId) => {
  const map = { 0: '超级管理员', 1: '管理员', 2: '普通用户' };
  return map[roleId] || '未知';
};
const getRoleType = (roleId) => {
  const map = { 0: 'danger', 1: 'primary', 2: 'info' };
  return map[roleId] || 'info';
};

// Privacy masking
const maskPhone = (phone) => {
  if (!phone || phone.length < 7) return phone || '-';
  return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4);
};
const maskEmail = (email) => {
  if (!email || !email.includes('@')) return email || '-';
  const [local, domain] = email.split('@');
  const maskedLocal = local.length > 2 ? local.substring(0, 2) + '***' : local;
  return maskedLocal + '@' + domain;
};

// API Calls
const loadPost = () => {
  axios.post('http://localhost:8090/user/listPage', {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: { name: searchName.value }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      total.value = res.data.total;
    } else {
      ElMessage.error('获取用户列表失败');
    }
  });
};

const reset = () => {
  searchName.value = '';
  loadPost();
};

const add = () => {
  dialogVisible.value = true;
  nextTick(() => {
    form.value = { id: '', no: '', name: '', password: '123', age: 25, phone: '', sex: 1, roleId: 2 };
  });
};

const mod = (row) => {
  dialogVisible.value = true;
  nextTick(() => {
    // 深拷贝，但不包含密码
    const { password, ...rest } = row;
    form.value = JSON.parse(JSON.stringify(rest));
  });
};

const save = async () => {
  saving.value = true;
  try {
    const res = await axios.post('http://localhost:8090/user/saveOrMod', form.value);
    if (res.data.code === 200) {
      ElMessage.success('保存成功');
      dialogVisible.value = false;
      loadPost();
    } else {
      ElMessage.error('保存失败');
    }
  } finally {
    saving.value = false;
  }
};

const del = (id) => {
  axios.get('http://localhost:8090/user/delete?id=' + id).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('删除成功');
      loadPost();
    } else {
      ElMessage.error('删除失败');
    }
  });
};

const resetPassword = (id) => {
  axios.get('http://localhost:8090/user/resetPassword?id=' + id).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('密码已重置为 123');
    } else {
      ElMessage.error('重置失败');
    }
  });
};

const handleSizeChange = (val) => { pageSize.value = val; loadPost(); };
const handleCurrentChange = (val) => { pageNum.value = val; loadPost(); };

onMounted(() => { loadPost(); });
</script>

<style scoped>
.user-manage-page {
  min-height: 100%;
  background: #f8fafc;
}

/* Hero */
.user-hero {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  margin-top: -24px;
  display: flex; align-items: center; justify-content: center; text-align: center;
}
.hero-overlay {
  position: absolute; inset: 0;
  background-image: radial-gradient(circle at 20% 30%, rgba(239, 68, 68, 0.1), transparent 40%),
                    radial-gradient(circle at 80% 70%, rgba(99, 102, 241, 0.1), transparent 40%);
}
.hero-content { position: relative; z-index: 2; color: #fff; }
.page-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; }
.page-subtitle { color: #94a3b8; font-size: 15px; margin: 0; }

/* Filter Bar */
.filter-bar-wrapper {
  max-width: 1400px;
  margin: -50px auto 24px;
  padding: 0 24px;
  position: relative; z-index: 5;
}
.filter-bar {
  background: #fff;
  border-radius: 16px;
  padding: 12px 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  display: flex; justify-content: space-between; align-items: center;
  flex-wrap: wrap; gap: 16px;
}
.filter-left, .filter-right { display: flex; align-items: center; gap: 12px; }
.filter-item {
  background: #f8fafc;
  border-radius: 10px;
  display: flex; align-items: center;
  padding: 0 12px; height: 44px;
}
.search-icon { color: #94a3b8; margin-right: 8px; }
.custom-input {
  border: none; background: transparent; outline: none;
  font-size: 14px; width: 180px; color: #333;
}
.action-btn { height: 42px; border-radius: 10px; font-weight: 500; display: flex; align-items: center; gap: 6px; }

/* Main Content */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

/* Stats */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.stat-card {
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 16px;
  padding: 20px 24px;
  display: flex; align-items: center; gap: 16px;
  color: #fff;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}
.stat-card.admin { background: linear-gradient(135deg, #f59e0b, #f97316); box-shadow: 0 4px 20px rgba(245, 158, 11, 0.3); }
.stat-card.user { background: linear-gradient(135deg, #10b981, #14b8a6); box-shadow: 0 4px 20px rgba(16, 185, 129, 0.3); }
.stat-card .el-icon { font-size: 36px; opacity: 0.9; }
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.85; }

/* Table Card */
.table-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}
.user-cell { display: flex; align-items: center; gap: 10px; }
.masked-phone, .masked-email { color: #64748b; font-family: monospace; }
.action-btns { display: flex; gap: 8px; }

/* Pagination */
.pagination-section { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>