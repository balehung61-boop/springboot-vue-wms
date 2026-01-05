<template>
  <div class="storage-page">
    <!-- 1. Hero Header 区域 -->
    <div class="storage-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="page-title">{{ isAdmin ? '仓库管理' : '仓库列表' }}</h1>
        <p class="page-subtitle">{{ isAdmin ? '全方位掌控您的仓储资源，高效配置与调度' : '浏览所有可用仓库资源，实时掌握库存动态' }}</p>
        
        <!-- 悬浮搜索栏 -->
        <div class="hero-search-bar">
          <el-icon class="search-icon"><Search /></el-icon>
          <input 
            type="text" 
            v-model="searchName"
            class="hero-search-input"
            placeholder="搜索仓库名称、地址或负责人..."
            @keyup.enter="loadPost"
          />
          <el-button type="primary" class="hero-search-btn" @click="loadPost">
            搜 索
          </el-button>
        </div>
      </div>
    </div>

    <!-- 2. 统计概览区域 (重叠效果) -->
    <div class="stats-container">
      <div class="stat-item card-purple">
        <div class="stat-icon-wrapper">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-text">
          <div class="stat-val">{{ total }}</div>
          <div class="stat-label">仓库总数</div>
        </div>
      </div>
      <div class="stat-item card-green">
        <div class="stat-icon-wrapper">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-text">
          <div class="stat-val">{{ total }}</div>
          <div class="stat-label">运营中</div>
        </div>
      </div>
      <div class="stat-item card-blue">
        <div class="stat-icon-wrapper">
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <div class="stat-text">
          <div class="stat-val">{{ totalArea }}</div>
          <div class="stat-label">总面积 (m²)</div>
        </div>
      </div>
      <!-- 管理员新增按钮 -->
      <div class="stat-item action-card" v-if="isAdmin" @click="add">
        <div class="action-content">
          <div class="action-icon-wrapper">
            <el-icon><Plus /></el-icon>
          </div>
          <span class="action-text">新增仓库</span>
        </div>
      </div>
    </div>

    <!-- 3. 仓库列表核心区域 -->
    <div class="main-content-wrapper">
      <div class="storage-grid-container" @click.self="clearSelection">
        <transition-group name="list">
          <div 
            class="modern-storage-card" 
            v-for="storage in tableData" 
            :key="storage.id"
            :class="{ 'is-active': selectedStorageId === storage.id }"
            @click.stop="selectStorage(storage)"
          >
            <!-- 顶部装饰条 -->
            <div class="card-bar" :class="getTypeClass(storage.type)"></div>
            
            <div class="card-body">
              <div class="card-header-row">
                <div class="icon-box" :class="getTypeClass(storage.type)">
                  <el-icon :size="24"><OfficeBuilding /></el-icon>
                </div>
                <!-- 类型标签 -->
                <span class="type-badge" :class="getTypeClass(storage.type)">
                  {{ storage.type || '普通仓库' }}
                </span>
              </div>

              <h3 class="storage-name">{{ storage.name }}</h3>
              <p class="storage-desc">{{ storage.remark || '暂无详细描述...' }}</p>

              <!-- 信息列表 -->
              <div class="info-list">
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span class="info-text">{{ storage.address || '暂无地址' }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span class="info-text">{{ storage.manager || '待定负责人' }}</span>
                </div>
                <div class="info-row highlight-row">
                  <el-icon><Box /></el-icon>
                  <span class="info-text highlight-text">{{ storage.area ? storage.area + ' m²' : '-' }}</span>
                </div>
              </div>

              <!-- 底部操作栏 (Hover显示) -->
              <div class="card-actions" v-if="isAdmin">
                <el-button link type="primary" @click.stop="mod(storage)">编辑</el-button>
                <el-divider direction="vertical" />
                <el-popconfirm title="确认删除该仓库？不可恢复" @confirm="del(storage.id)">
                  <template #reference>
                    <el-button link type="danger" @click.stop>删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </div>
        </transition-group>

        <!-- 空状态 -->
        <el-empty v-if="tableData.length === 0" description="暂无相关仓库数据" :image-size="120" />
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="pageNum"
          :page-sizes="[9, 12, 24, 36]"
          :page-size="pageSize"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 详情弹窗 (恢复并现代化) -->
    <Teleport to="body">
      <transition name="modal-fade">
        <div class="storage-modal-overlay" v-if="selectedStorageId" @click="clearSelection">
          <div class="storage-details-modal" @click.stop>
            <button class="modal-close-btn" @click="clearSelection">
              <el-icon :size="20"><Close /></el-icon>
            </button>

            <!-- 头部信息 -->
            <div class="details-header">
              <div class="details-icon-wrapper" :class="getTypeClass(selectedStorage.type)">
                <el-icon :size="32"><OfficeBuilding /></el-icon>
              </div>
              <div class="details-title-box">
                <h2 class="details-title">{{ selectedStorage.name }}</h2>
                <el-tag :type="getTypeColor(selectedStorage.type)" effect="dark" round>
                  {{ selectedStorage.type || '普通仓库' }}
                </el-tag>
              </div>
            </div>

            <!-- 核心信息卡片 -->
            <div class="details-grid">
              <div class="detail-card">
                <div class="detail-icon"><Location /></div>
                <div class="detail-info">
                  <span class="label">仓库地址</span>
                  <span class="value">{{ selectedStorage.address || '暂无地址' }}</span>
                </div>
              </div>
              <div class="detail-card">
                <div class="detail-icon"><Phone /></div>
                <div class="detail-info">
                  <span class="label">联系电话</span>
                  <span class="value">{{ selectedStorage.phone || '暂无电话' }}</span>
                </div>
              </div>
              <div class="detail-card">
                <div class="detail-icon"><User /></div>
                <div class="detail-info">
                  <span class="label">负责人</span>
                  <span class="value">{{ selectedStorage.manager || '待定' }}</span>
                </div>
              </div>
              <div class="detail-card">
                <div class="detail-icon"><Box /></div>
                <div class="detail-info">
                  <span class="label">仓库面积</span>
                  <span class="value">{{ selectedStorage.area ? selectedStorage.area + ' m²' : '-' }}</span>
                </div>
              </div>
            </div>

            <!-- 描述信息 -->
            <div class="details-desc-box">
              <h3 class="desc-header"><el-icon><Document /></el-icon> 仓库介绍</h3>
              <p class="desc-text">{{ selectedStorage.remark || '暂无详细介绍...' }}</p>
            </div>

            <!-- 底部按钮 -->
            <div class="details-footer">
              <el-button @click="clearSelection">关闭</el-button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 弹窗：CRUD -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑仓库信息' : '创建新仓库'"
      width="640px"
      class="modern-dialog"
      align-center
    >
      <el-form :model="form" label-width="90px" :rules="rules" ref="formRef" class="custom-form">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库类型" prop="type">
              <el-select v-model="form.type" placeholder="选择类型" style="width:100%">
                <el-option label="普通仓库" value="普通仓库" />
                <el-option label="电商仓库" value="电商仓库" />
                <el-option label="冷链仓库" value="冷链仓库" />
                <el-option label="保税仓库" value="保税仓库" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入具体地址" :prefix-icon="Location" />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="负责人" prop="manager">
              <el-input v-model="form.manager" placeholder="姓名" :prefix-icon="User" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="电话号码" :prefix-icon="Phone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="仓库面积" prop="area">
          <el-input v-model="form.area" placeholder="数字">
            <template #append>平方米 (m²)</template>
          </el-input>
        </el-form-item>

        <el-form-item label="备注说明" prop="remark">
          <el-input type="textarea" v-model="form.remark" :rows="3" placeholder="填写仓库的其他相关信息..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save" :loading="saving">确认保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { 
  OfficeBuilding, Search, Plus, Location, Phone, User, Box, 
  Edit, Delete, Check, DataAnalysis, Close, Document
} from '@element-plus/icons-vue';

const user = JSON.parse(sessionStorage.getItem('user')) || null;
const isAdmin = computed(() => user && user.roleId !== 2);

const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(9); // 增加默认条数，适应大屏
const total = ref(0);
const searchName = ref('');
const dialogVisible = ref(false);
const isEdit = ref(false);
const saving = ref(false);
const formRef = ref(null);

const form = ref({
  id: '', name: '', address: '', phone: '', manager: '', area: '', type: '', remark: ''
});

const rules = {
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
};

const totalArea = computed(() => {
  return tableData.value.reduce((sum, item) => sum + (parseInt(item.area) || 0), 0);
});

// 计算样式类名
const getTypeClass = (type) => {
  const map = {
    '电商仓库': 'type-purple',
    '冷链仓库': 'type-blue',
    '保税仓库': 'type-orange',
    '普通仓库': 'type-gray'
  };
  return map[type] || 'type-gray';
};

// 获取Tag颜色类型
const getTypeColor = (type) => {
  const map = {
    '电商仓库': '', // purple maps to primary/default or custom style, using default for now or '' if using custom class
    '冷链仓库': '', 
    '保税仓库': 'warning',
    '普通仓库': 'info'
  };
    // 为了匹配设计，我们可以利用 effect="dark" 配合自定义颜色，或者直接映射到 element plus 的标准色
    const elMap = {
        '电商仓库': 'success', // or primary
        '冷链仓库': '', // primary default
        '保税仓库': 'warning',
        '普通仓库': 'info'
    };
  return elMap[type] || '';
};

const loadPost = () => {
  axios.post('http://localhost:8090/storage/listPage', {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: { name: searchName.value }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      total.value = res.data.total;
    } else {
      ElMessage.error('获取数据失败');
    }
  });
};

const add = () => {
  isEdit.value = false;
  form.value = { id: '', name: '', address: '', phone: '', manager: '', area: '', type: '普通仓库', remark: '' };
  dialogVisible.value = true;
};

const mod = (row) => {
  isEdit.value = true;
  form.value = { ...row };
  dialogVisible.value = true;
};

const save = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    saving.value = true;
    const res = await axios.post('http://localhost:8090/storage/saveOrMod', form.value);
    saving.value = false;
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '已更新' : '已创建');
      dialogVisible.value = false;
      loadPost();
    } else {
      ElMessage.error('操作失败');
    }
  } catch (e) {
    saving.value = false;
  }
};

const del = (id) => {
  axios.get('http://localhost:8090/storage/delete?id=' + id).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('删除成功');
      loadPost();
    } else {
      ElMessage.error('删除失败');
    }
  });
};

const selectedStorageId = ref(null);
const selectedStorage = ref({}); // 新增

const selectStorage = (storage) => {
  selectedStorageId.value = storage.id;
  selectedStorage.value = storage; // 赋值
};
const clearSelection = () => {
  selectedStorageId.value = null;
  selectedStorage.value = {};
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadPost();
};
const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadPost();
};

onMounted(() => {
  loadPost();
});
</script>

<style scoped>
.storage-page {
  min-height: 100%;
  background-color: #f8fafc;
}

/* Hero Header */
.storage-hero {
  position: relative;
  height: 320px;
  background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  /* 破除父级padding */
  width: 100%;
  margin-top: -24px; /* 假设父级有24px padding-top */
  margin-bottom: 60px; /* 留出空间给 stats */
}

.hero-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.15), transparent 40%),
                    radial-gradient(circle at 80% 50%, rgba(118, 75, 162, 0.15), transparent 40%);
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  padding: 0 20px;
}

.page-title {
  color: #fff;
  font-size: 36px;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.page-subtitle {
  color: rgba(255,255,255,0.7);
  font-size: 16px;
  margin: 0 0 40px;
}

/* Floating Search Bar */
.hero-search-bar {
  max-width: 600px;
  margin: 0 auto;
  background: rgba(255,255,255,0.95);
  border-radius: 50px;
  padding: 6px;
  display: flex;
  align-items: center;
  box-shadow: 0 15px 40px rgba(0,0,0,0.2);
  transition: transform 0.3s ease;
}

.hero-search-bar:focus-within {
  transform: translateY(-2px);
  background: #fff;
}

.hero-search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 0 16px;
  font-size: 16px;
  color: #333;
}

.search-icon {
  margin-left: 16px;
  color: #999;
  font-size: 20px;
}

.hero-search-btn {
  height: 48px;
  border-radius: 24px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* Stats Container */
.stats-container {
  max-width: 1200px;
  margin: -100px auto 40px; /* 向上重叠 */
  position: relative;
  z-index: 3;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  padding: 0 24px;
}

.stat-item {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.06);
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
}

.stat-icon-wrapper {
  width: 60px; height: 60px;
  border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px;
  color: #fff;
}

.card-purple .stat-icon-wrapper { background: linear-gradient(135deg, #a78bfa, #8b5cf6); }
.card-green .stat-icon-wrapper { background: linear-gradient(135deg, #34d399, #10b981); }
.card-blue .stat-icon-wrapper { background: linear-gradient(135deg, #60a5fa, #3b82f6); }

/* Action Card */
.action-card {
  background: linear-gradient(135deg, #1f1f1f, #333);
  cursor: pointer;
  border: 1px solid rgba(255,255,255,0.1);
}
.action-card .action-content {
  display: flex; align-items: center; gap: 16px; width: 100%;
}
.action-icon-wrapper {
  width: 48px; height: 48px;
  border-radius: 50%;
  background: rgba(255,255,255,0.15);
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  font-size: 24px;
}
.action-text {
  color: #fff;
  font-weight: 600;
  font-size: 18px;
}

.stat-val { font-size: 24px; font-weight: 800; color: #1e293b; }
.stat-label { font-size: 13px; color: #64748b; margin-top: 4px; }

/* Main Content */
.main-content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 60px;
}

.storage-grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

/* Modern Storage Card */
.modern-storage-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  flex-direction: column;
  border: 1px solid #f1f5f9;
  cursor: default;
}

.modern-storage-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.card-bar { height: 6px; width: 100%; }
.type-purple { background: #8b5cf6; color: #8b5cf6; } 
.type-blue { background: #3b82f6; color: #3b82f6; } /* 冷链 */
.type-orange { background: #f59e0b; color: #f59e0b; } /* 保税 */
.type-gray { background: #64748b; color: #64748b; } 

/* Card Body */
.card-body {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.icon-box {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  background: #f3f4f6; /* default */
}

/* Apply light background colors for icons based on type */
.icon-box.type-purple { background: #f3e8ff; }
.icon-box.type-blue { background: #dbeafe; }
.icon-box.type-orange { background: #fef3c7; }
.icon-box.type-gray { background: #f1f5f9; }

.type-badge {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  background: #f1f5f9;
  font-weight: 500;
}
.type-badge.type-purple { background: #f3e8ff; color: #7c3aed; }
.type-badge.type-blue { background: #dbeafe; color: #2563eb; }
.type-badge.type-orange { background: #fef3c7; color: #d97706; }
.type-badge.type-gray { background: #f1f5f9; color: #475569; }

.storage-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px;
}

.storage-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 20px;
  line-height: 1.5;
  height: 42px; /* 2 lines */
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #64748b;
}

.info-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.highlight-row { margin-top: 4px; }
.highlight-text { 
  font-weight: 600; 
  color: #0f172a; 
  background: #f8fafc; 
  padding: 4px 8px; 
  border-radius: 6px; 
}

/* Actions */
.card-actions {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
}

.modern-storage-card:hover .card-actions {
  opacity: 1;
  transform: translateY(0);
}

.pagination-section {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

/* Dialog */
.modern-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
}
.modern-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
}
.modern-dialog :deep(.el-dialog__body) {
  padding: 24px;
}
.modern-dialog :deep(.el-dialog__footer) {
  padding: 24px;
  background: #f8fafc;
}

/* Detail Modal Styles */
.storage-modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(6px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.storage-details-modal {
  background: #fff;
  width: 100%;
  max-width: 700px;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  position: relative;
  overflow: hidden;
  padding: 32px;
  animation: modalPop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalPop {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.modal-close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #f1f5f9;
  border: none;
  width: 36px; height: 36px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}
.modal-close-btn:hover { background: #e2e8f0; color: #1e293b; }

.details-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.details-icon-wrapper {
  width: 72px; height: 72px;
  border-radius: 20px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}
.details-icon-wrapper.type-purple { background: linear-gradient(135deg, #a78bfa, #7c3aed); }
.details-icon-wrapper.type-blue { background: linear-gradient(135deg, #60a5fa, #2563eb); }
.details-icon-wrapper.type-orange { background: linear-gradient(135deg, #fcd34d, #d97706); }
.details-icon-wrapper.type-gray { background: linear-gradient(135deg, #94a3b8, #475569); }

.details-title { font-size: 28px; font-weight: 700; color: #0f172a; margin: 0 0 8px; }

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.detail-card {
  background: #f8fafc;
  padding: 16px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-icon {
  width: 40px; height: 40px;
  background: #fff;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: #64748b;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.detail-info {
  display: flex; flex-direction: column;
}
.detail-info .label { font-size: 12px; color: #94a3b8; }
.detail-info .value { font-size: 15px; color: #1e293b; font-weight: 500; }

.details-desc-box {
  background: #f8fafc;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 32px;
}

.desc-header {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 12px;
  display: flex; align-items: center; gap: 8px;
}

.desc-text {
  font-size: 15px;
  line-height: 1.6;
  color: #334155;
  margin: 0;
}

.details-footer {
  display: flex; justify-content: flex-end; gap: 12px;
}

/* Modal Transition */
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>