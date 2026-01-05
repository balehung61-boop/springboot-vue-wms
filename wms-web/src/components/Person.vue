<template>
  <div class="profile-page">
    <!-- 1. Hero Header -->
    <div class="profile-hero">
      <div class="hero-pattern"></div>
      <div class="hero-content">
        <h1 class="hero-title">个人中心</h1>
        <p class="hero-subtitle">管理您的账户信息与偏好设置</p>
      </div>
    </div>

    <!-- 2. Main Content Area -->
    <div class="profile-container">
      
      <!-- Left: Profile Card -->
      <div class="profile-sidebar">
        <div class="profile-card">
          <!-- Avatar Section -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar 
                :size="120" 
                :src="form.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
                class="main-avatar"
              />
              <el-upload
                action="http://localhost:8090/file/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                class="avatar-upload-trigger"
              >
                <div class="camera-btn">
                  <el-icon><Camera /></el-icon>
                </div>
              </el-upload>
            </div>
          </div>

          <!-- User Info -->
          <div class="user-info">
            <h2 class="user-name">{{ form.name }}</h2>
            <div class="user-role">
              <el-tag :type="form.roleId === 0 ? 'danger' : 'info'" effect="dark" round>
                {{ form.roleId === 0 ? '超级管理员' : '普通用户' }}
              </el-tag>
            </div>
            <p class="user-bio">{{ form.description || '这个人很懒，什么都没写~' }}</p>
          </div>

          <!-- Quick Stats -->
          <div class="user-stats">
            <div class="stat-item">
              <el-icon><Location /></el-icon>
              <span>{{ addressList.length }} 个地址</span>
            </div>
            <div class="stat-item">
              <el-icon><Message /></el-icon>
              <span>{{ form.email || '未设置' }}</span>
            </div>
            <div class="stat-item">
              <el-icon><Phone /></el-icon>
              <span>{{ form.phone || '未设置' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Tab Panels -->
      <div class="profile-main">
        <div class="tabs-card">
          <!-- Custom Tab Header -->
          <div class="custom-tabs">
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'info' }" 
              @click="activeTab = 'info'"
            >
              <el-icon><User /></el-icon>
              <span>基础信息</span>
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'security' }" 
              @click="activeTab = 'security'"
            >
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'address' }" 
              @click="activeTab = 'address'"
            >
              <el-icon><LocationInformation /></el-icon>
              <span>收货地址</span>
            </button>
          </div>

          <!-- Tab Content -->
          <div class="tab-content">
            <!-- Tab 1: Basic Info -->
            <transition name="fade" mode="out-in">
              <div v-if="activeTab === 'info'" class="tab-pane" key="info">
                <h3 class="section-title">基础资料</h3>
                <el-form :model="form" label-width="100px" class="modern-form">
                  <el-form-item label="账号">
                    <el-input v-model="form.no" disabled prefix-icon="User" />
                  </el-form-item>
                  <el-form-item label="姓名">
                    <el-input v-model="form.name" placeholder="请输入姓名" />
                  </el-form-item>
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="电话">
                        <el-input v-model="form.phone" placeholder="手机号码" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="邮箱">
                        <el-input v-model="form.email" placeholder="电子邮箱" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-form-item label="性别">
                    <el-radio-group v-model="form.sex" class="gender-radio">
                      <el-radio :label="1" border>男</el-radio>
                      <el-radio :label="0" border>女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="个人简介">
                    <el-input type="textarea" v-model="form.description" rows="4" placeholder="介绍一下自己吧..." />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="saveUser" :loading="saving" class="save-btn">
                      <el-icon><Check /></el-icon> 保存修改
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </transition>

            <!-- Tab 2: Security -->
            <transition name="fade" mode="out-in">
              <div v-if="activeTab === 'security'" class="tab-pane" key="security">
                <h3 class="section-title">修改密码</h3>
                <el-form label-width="100px" class="modern-form">
                  <el-form-item label="原密码">
                    <el-input type="password" v-model="oldPassword" placeholder="请输入当前密码" show-password />
                  </el-form-item>
                  <el-form-item label="新密码">
                    <el-input type="password" v-model="form.password" placeholder="请输入新密码" show-password />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="danger" @click="saveUser" :loading="saving" class="save-btn">
                      <el-icon><Lock /></el-icon> 更新密码
                    </el-button>
                  </el-form-item>
                </el-form>
                
                <div class="security-tips">
                  <h4><el-icon><WarningFilled /></el-icon> 安全提示</h4>
                  <ul>
                    <li>密码建议至少8位，包含字母和数字</li>
                    <li>定期更换密码可提升账户安全性</li>
                    <li>请勿与他人共享您的账户密码</li>
                  </ul>
                </div>
              </div>
            </transition>

            <!-- Tab 3: Address -->
            <transition name="fade" mode="out-in">
              <div v-if="activeTab === 'address'" class="tab-pane" key="address">
                <div class="address-header">
                  <h3 class="section-title">我的收货地址</h3>
                  <el-button type="primary" @click="addAddress" class="add-addr-btn">
                    <el-icon><Plus /></el-icon> 新增地址
                  </el-button>
                </div>

                <div class="address-grid" v-if="addressList.length > 0">
                  <div 
                    class="address-card" 
                    v-for="addr in addressList" 
                    :key="addr.id"
                    :class="{ 'is-default': addr.isDefault === 'Y' }"
                  >
                    <div class="addr-badge" v-if="addr.isDefault === 'Y'">默认</div>
                    <div class="addr-header">
                      <span class="addr-name">{{ addr.linkman }}</span>
                      <span class="addr-phone">{{ addr.phone }}</span>
                    </div>
                    <p class="addr-detail">{{ addr.address }}</p>
                    <div class="addr-actions">
                      <el-button link type="primary" @click="editAddress(addr)">
                        <el-icon><Edit /></el-icon> 编辑
                      </el-button>
                      <el-popconfirm title="确定删除此地址吗？" @confirm="delAddress(addr.id)">
                        <template #reference>
                          <el-button link type="danger">
                            <el-icon><Delete /></el-icon> 删除
                          </el-button>
                        </template>
                      </el-popconfirm>
                    </div>
                  </div>
                </div>

                <el-empty v-else description="暂无收货地址，快去添加吧~" :image-size="120" />
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

    <!-- Address Dialog -->
    <el-dialog v-model="addrDialogVisible" :title="addrForm.id ? '编辑地址' : '新增地址'" width="450px" class="modern-dialog" align-center>
      <el-form :model="addrForm" label-width="80px" class="modern-form">
        <el-form-item label="联系人">
          <el-input v-model="addrForm.linkman" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="addrForm.phone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input type="textarea" v-model="addrForm.address" rows="3" placeholder="省/市/区/街道/门牌号" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addrForm.isDefault" active-value="Y" inactive-value="N" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addrDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="addrSaving">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { 
  Camera, Location, Message, Phone, User, Lock, LocationInformation, 
  Check, Plus, Edit, Delete, WarningFilled 
} from '@element-plus/icons-vue';

const user = JSON.parse(sessionStorage.getItem('user'));
const activeTab = ref('info');
const form = ref({ ...user });
const oldPassword = ref('');
const saving = ref(false);

// Address
const addressList = ref([]);
const addrDialogVisible = ref(false);
const addrSaving = ref(false);
const addrForm = ref({ id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N' });

// Save User Info
const saveUser = async () => {
  saving.value = true;
  try {
    const res = await axios.post('http://localhost:8090/user/mod', form.value);
    if (res.data.code === 200) {
      ElMessage.success('保存成功');
      sessionStorage.setItem('user', JSON.stringify(form.value));
    } else {
      ElMessage.error('保存失败');
    }
  } finally {
    saving.value = false;
  }
};

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    form.value.avatar = res.data;
    saveUser();
    ElMessage.success('头像已更新');
  } else {
    ElMessage.error('头像上传失败');
  }
};

// Address Management
const loadAddress = () => {
  axios.get(`http://localhost:8090/address/list?userId=${user.id}`).then(res => {
    addressList.value = res.data.data || [];
  });
};

const addAddress = () => {
  addrDialogVisible.value = true;
  addrForm.value = { id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N' };
};

const editAddress = (row) => {
  addrDialogVisible.value = true;
  addrForm.value = JSON.parse(JSON.stringify(row));
};

const saveAddress = async () => {
  addrSaving.value = true;
  try {
    const res = await axios.post('http://localhost:8090/address/saveOrMod', addrForm.value);
    if (res.data.code === 200) {
      ElMessage.success('地址保存成功');
      addrDialogVisible.value = false;
      loadAddress();
    }
  } finally {
    addrSaving.value = false;
  }
};

const delAddress = (id) => {
  axios.get(`http://localhost:8090/address/delete?id=${id}`).then(res => {
    if (res.data.code === 200) {
      ElMessage.success('删除成功');
      loadAddress();
    }
  });
};

onMounted(() => {
  loadAddress();
});
</script>

<style scoped>
.profile-page {
  min-height: 100%;
  background: linear-gradient(180deg, #f8fafc 0%, #eef2f7 100%);
}

/* Hero */
.profile-hero {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  margin-top: -24px;
  overflow: hidden;
}
.hero-pattern {
  position: absolute; inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%239C92AC' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}
.hero-content {
  position: relative; z-index: 2;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 100%; color: #fff; text-align: center;
}
.hero-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; }
.hero-subtitle { color: #94a3b8; font-size: 15px; margin: 0; }

/* Container */
.profile-container {
  max-width: 1200px;
  margin: -60px auto 40px;
  padding: 0 24px;
  display: flex;
  gap: 24px;
  position: relative; z-index: 5;
}

/* Left Sidebar */
.profile-sidebar { width: 320px; flex-shrink: 0; }
.profile-card {
  background: #fff;
  border-radius: 20px;
  padding: 32px 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  text-align: center;
}

.avatar-section { position: relative; margin-bottom: 20px; }
.avatar-wrapper { position: relative; display: inline-block; }
.main-avatar {
  border: 4px solid #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  transition: transform 0.3s;
}
.main-avatar:hover { transform: scale(1.05); }
.avatar-upload-trigger { position: absolute; bottom: 0; right: 0; }
.camera-btn {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; cursor: pointer;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transition: all 0.3s;
}
.camera-btn:hover { transform: scale(1.1); }

.user-info { margin-bottom: 24px; }
.user-name { font-size: 24px; font-weight: 700; color: #1e293b; margin: 0 0 12px; }
.user-role { margin-bottom: 12px; }
.user-bio { font-size: 14px; color: #64748b; margin: 0; line-height: 1.6; }

.user-stats {
  border-top: 1px solid #f1f5f9;
  padding-top: 20px;
  display: flex; flex-direction: column; gap: 12px;
}
.stat-item {
  display: flex; align-items: center; gap: 10px;
  font-size: 13px; color: #64748b;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 8px;
  transition: all 0.2s;
}
.stat-item:hover { background: #f1f5f9; transform: translateX(4px); }
.stat-item .el-icon { color: #3b82f6; }

/* Right Main */
.profile-main { flex: 1; }
.tabs-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  overflow: hidden;
}

/* Custom Tabs */
.custom-tabs {
  display: flex;
  background: #f8fafc;
  padding: 8px;
  gap: 8px;
}
.tab-btn {
  flex: 1;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  padding: 14px 20px;
  background: transparent;
  border: none;
  border-radius: 12px;
  font-size: 14px; font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;
}
.tab-btn:hover { background: #e2e8f0; color: #334155; }
.tab-btn.active {
  background: #fff;
  color: #3b82f6;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

/* Tab Content */
.tab-content { padding: 32px; }
.tab-pane { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.section-title {
  font-size: 18px; font-weight: 600; color: #1e293b;
  margin: 0 0 24px;
  display: flex; align-items: center; gap: 8px;
}
.section-title::before {
  content: '';
  width: 4px; height: 20px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  border-radius: 2px;
}

/* Modern Form */
.modern-form :deep(.el-input__wrapper),
.modern-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0 !important;
  transition: all 0.2s;
}
.modern-form :deep(.el-input__wrapper:focus-within),
.modern-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px #3b82f6 !important;
}
.save-btn {
  height: 44px; border-radius: 10px; font-weight: 500;
  display: flex; align-items: center; gap: 6px;
}

/* Security Tips */
.security-tips {
  margin-top: 32px; padding: 20px;
  background: #fffbeb; border-radius: 12px;
  border: 1px solid #fde68a;
}
.security-tips h4 {
  margin: 0 0 12px; font-size: 14px; color: #92400e;
  display: flex; align-items: center; gap: 6px;
}
.security-tips ul { margin: 0; padding-left: 20px; }
.security-tips li { font-size: 13px; color: #a16207; line-height: 1.8; }

/* Address */
.address-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
}
.add-addr-btn { border-radius: 10px; }

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.address-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 20px;
  position: relative;
  transition: all 0.3s;
}
.address-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  transform: translateY(-2px);
}
.address-card.is-default { border-color: #3b82f6; background: #eff6ff; }
.addr-badge {
  position: absolute; top: 12px; right: 12px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: #fff; font-size: 11px; padding: 4px 10px;
  border-radius: 20px; font-weight: 500;
}
.addr-header { margin-bottom: 8px; }
.addr-name { font-weight: 600; color: #1e293b; margin-right: 12px; }
.addr-phone { color: #64748b; font-size: 13px; }
.addr-detail { font-size: 13px; color: #64748b; margin: 0 0 16px; line-height: 1.5; }
.addr-actions { display: flex; gap: 12px; }

/* Transitions */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px); }
</style>