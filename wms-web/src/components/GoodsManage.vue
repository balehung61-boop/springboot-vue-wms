<template>
  <div class="goods-page">
    <!-- 1. Hero Header -->
    <div class="goods-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="page-title">商品管理中心</h1>
        <p class="page-subtitle">高效管理您的库存资产，实时监控商品动态</p>
      </div>
    </div>

    <!-- 2. 悬浮筛选栏 -->
    <div class="filter-bar-wrapper">
      <div class="filter-bar">
        <div class="filter-left">
          <div class="filter-item search-item">
            <el-icon class="search-icon"><Search /></el-icon>
            <input 
              v-model="name" 
              placeholder="搜索商品名称..." 
              class="custom-input"
              @keyup.enter="loadPost"
            />
          </div>
          
          <div class="filter-item select-item">
             <el-select 
               v-model="selectedStorage" 
               placeholder="所有仓库" 
               clearable 
               @change="loadPost"
               class="custom-select"
               popper-class="custom-dropdown"
             >
              <el-option
                v-for="item in storageData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </div>

          <div class="filter-item select-item">
            <el-select 
              v-model="selectedCategory" 
              placeholder="所有分类" 
              clearable 
              @change="loadPost"
              class="custom-select"
              popper-class="custom-dropdown"
            >
              <el-option
                v-for="item in goodsTypeData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </div>
        </div>

        <div class="filter-right">
          <el-button type="primary" class="action-btn" @click="loadPost">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button class="action-btn" @click="reset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
          <el-button 
            v-if="user && user.roleId != 2" 
            type="success" 
            class="action-btn add-btn" 
            @click="add"
          >
            <el-icon><Plus /></el-icon> 新增商品
          </el-button>
        </div>
      </div>
    </div>

    <!-- 3. 商品卡片网格 -->
    <div class="main-content">
      <div class="goods-grid">
        <transition-group name="list">
          <div class="goods-card" v-for="item in tableData" :key="item.id">
            <!-- 卡片顶部：图片/图标区域 -->
            <!-- 卡片顶部：图片/图标区域 -->
            <div class="card-image-section">
              <!-- 图片展示 (修改为 contain 以完整显示) -->
              <el-image 
                v-if="item.image" 
                :src="item.image" 
                fit="contain" 
                class="goods-real-img"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <!-- 缺省图标 -->
              <div v-else class="goods-icon-wrapper" :class="getCategoryClass(item.goodstypeId)">
                <el-icon :size="48"><Goods /></el-icon>
              </div>
              <div class="stock-badge" :class="item.count < 10 ? 'low-stock' : 'normal-stock'">
                {{ item.count < 10 ? '库存告急' : '库存充足' }}
              </div>
              
              <!-- 悬浮操作层 (仅管理员) -->
              <div class="hover-actions" v-if="user && user.roleId != 2">
                 <el-tooltip content="入库" placement="top">
                    <button class="icon-btn in-btn" @click.stop="inGoods(item)">
                      <el-icon><Download /></el-icon>
                    </button>
                 </el-tooltip>
                 <el-tooltip content="出库" placement="top">
                    <button class="icon-btn out-btn" @click.stop="outGoods(item)">
                      <el-icon><Upload /></el-icon>
                    </button>
                 </el-tooltip>
                 <el-tooltip content="编辑" placement="top">
                    <button class="icon-btn edit-btn" @click.stop="mod(item)">
                      <el-icon><Edit /></el-icon>
                    </button>
                 </el-tooltip>
                  <el-popconfirm title="确定删除该商品吗？" @confirm="del(item.id)">
                    <template #reference>
                       <button class="icon-btn del-btn" @click.stop>
                        <el-icon><Delete /></el-icon>
                      </button>
                    </template>
                  </el-popconfirm>
              </div>
            </div>

            <!-- 卡片内容区域 -->
            <div class="card-content">
              <div class="goods-header">
                <h3 class="goods-name" :title="item.name">{{ item.name }}</h3>
                <span class="storage-tag">
                  <el-icon><OfficeBuilding /></el-icon>
                  {{ formatStorage(item) }}
                </span>
              </div>
              
              <div class="goods-meta">
                 <span class="category-tag">
                   <el-icon><Files /></el-icon>
                   {{ formatGoodsType(item) }}
                 </span>
                 <span class="price-tag">￥{{ item.price }}</span>
              </div>

              <div class="stock-info">
                <div class="stock-bar-bg">
                  <div 
                    class="stock-bar-fill" 
                    :style="{ width: Math.min((item.count / 500) * 100, 100) + '%' }"
                    :class="item.count < 50 ? 'bg-red' : 'bg-blue'"
                  ></div>
                </div>
                <span class="stock-text">库存: {{ item.count }}</span>
              </div>

              <p class="goods-remark">{{ item.remark || '暂无备注说明...' }}</p>
            </div>

            <!-- 普通用户操作 -->
            <div class="card-footer" v-if="user && user.roleId == 2">
              <el-button type="warning" class="cart-btn" @click="addCart(item)">
                <el-icon><ShoppingCart /></el-icon> 加入购物车
              </el-button>
            </div>
          </div>
        </transition-group>
        
        <!-- 空状态 -->
         <el-empty v-if="tableData.length === 0" description="暂无符合条件的商品" class="full-width-empty" />
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 弹窗：CRUD -->
    <el-dialog
        v-model="centerDialogVisible"
        :title="form.id ? '编辑商品' : '新增商品'"
        width="500px"
        class="modern-dialog"
        align-center>
      
      <el-form :model="form" label-width="80px" class="custom-form">
        <!-- 图片上传 -->
        <el-form-item label="商品图片">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8090/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="form.image" :src="form.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="物品名称">
          <el-input v-model="form.name" placeholder="请输入名称"></el-input>
        </el-form-item>

        <el-form-item label="所属仓库">
          <el-select v-model="form.storageId" placeholder="请选择仓库" style="width: 100%;">
            <el-option v-for="item in storageData" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="物品分类">
          <el-select v-model="form.goodstypeId" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="item in goodsTypeData" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价">
              <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input-number v-model="form.count" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" :rows="3"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="centerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 出入库弹窗 -->
    <el-dialog
        v-model="inOutVisible"
        :title="form1.flag === 1 ? '商品入库' : '商品出库'"
        width="400px"
        class="modern-dialog"
        align-center>
      <el-form label-width="80px" class="custom-form">
        <el-form-item label="物品名">
          <el-input v-model="form1.goodsname" disabled></el-input>
        </el-form-item>
        <el-form-item label="变动数量">
          <el-input-number v-model="form1.count" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form1.remark" placeholder="填写操作原因..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="inOutVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInOut">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 加入购物车弹窗 -->
    <el-dialog
        v-model="cartDialogVisible"
        title="加入购物车"
        width="400px"
        class="modern-dialog"
        align-center>
      
      <div class="cart-preview">
        <div class="cart-icon"><el-icon><ShoppingCart /></el-icon></div>
        <div class="cart-info">
          <h3>{{ cartForm.name }}</h3>
          <p class="cart-price">￥{{ cartForm.price }}</p>
        </div>
      </div>

      <el-form label-width="80px" class="custom-form" style="margin-top: 20px;">
        <el-form-item label="购买数量">
          <el-input-number v-model="cartForm.count" :min="1" :max="cartForm.stock" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="总金额">
           <div class="total-price">￥{{ (cartForm.price * cartForm.count).toFixed(2) }}</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cartDialogVisible = false">取消</el-button>
          <el-button type="warning" @click="confirmAddCart">加入购物车</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { 
  Search, Plus, Refresh, Edit, Delete, ShoppingCart, 
  Goods, OfficeBuilding, Files, Download, Upload, Picture 
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const user = JSON.parse(sessionStorage.getItem('user')) || null;

// 状态
const name = ref('');
const selectedStorage = ref(null);
const selectedCategory = ref(null);
const tableData = ref([]);
const storageData = ref([]);
const goodsTypeData = ref([]);

const pageNum = ref(1);
const pageSize = ref(12); // Grid layout default
const total = ref(0);

// 弹窗控制
const centerDialogVisible = ref(false);
const inOutVisible = ref(false);
const cartDialogVisible = ref(false);

const form = ref({ id:'', name:'', storageId:'', goodstypeId:'', price:0, count:0, remark:'', image:'' });

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data;
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
}
const form1 = ref({ goodsId:'', goodsname:'', count:'', remark:'', flag:1 });
const cartForm = ref({ goodsId:'', name:'', price:0, count:1, stock:0 });

// 检查全局搜索
const checkGlobalSearch = () => {
  const globalSearch = sessionStorage.getItem('globalSearch');
  if (globalSearch) {
    name.value = globalSearch;
    sessionStorage.removeItem('globalSearch');
  }
};

// 工具函数
const formatStorage = (row) => {
  let temp = storageData.value.find(item => item.id == row.storageId);
  return temp ? temp.name : '未知仓库';
}
const formatGoodsType = (row) => {
  let temp = goodsTypeData.value.find(item => item.id == row.goodstypeId);
  return temp ? temp.name : '未分类';
}

const getCategoryClass = (id) => {
  // 简单根据ID取余给颜色，实际可根据分类名映射
  const colors = ['icon-purple', 'icon-blue', 'icon-green', 'icon-orange'];
  return colors[id % colors.length] || 'icon-blue';
}

// API Calls
const loadPost = () => {
  axios.post('http://localhost:8090/goods/listPage', {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: {
      name: name.value,
      // 如果后端支持 storageId 和 goodstypeId 筛选可加，如果不支持目前只能前端过滤或保持现状
      // 假设当前只支持 name，这里保留现状。若要支持筛选需后端配合升级，
      // 这里为演示UI，暂时忽略筛选（或者如果后端支持 param map 传其他字段）
      goodstype: selectedCategory.value ? String(selectedCategory.value) : null,
      storage: selectedStorage.value ? String(selectedStorage.value) : null
    }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      total.value = res.data.total;
    } else {
      ElMessage.error('获取数据失败');
    }
  })
}

// 补充：为了能筛选，需要确认后端 Controller 是否接收 param 中的 key。
// 之前 Controller 代码只是 map.get("name")。
// 如果后端未改，这两个下拉框暂不起作用，但 UI 已经就位。

const loadStorage = () => axios.get('http://localhost:8090/storage/list').then(res => { if(res.data.code===200) storageData.value = res.data.data });
const loadGoodsType = () => axios.get('http://localhost:8090/goodstype/list').then(res => { if(res.data.code===200) goodsTypeData.value = res.data.data });

const reset = () => {
  name.value = '';
  selectedStorage.value = null;
  selectedCategory.value = null;
  loadPost();
}

const handleCurrentChange = (val) => { pageNum.value = val; loadPost(); }

// CRUD Operations
const add = () => {
  centerDialogVisible.value = true;
  nextTick(() => { form.value = {id:'', name:'', storageId:'', goodstypeId:'', price:0, count:0, remark:''}; })
}
const mod = (row) => {
  centerDialogVisible.value = true;
  nextTick(() => { form.value = JSON.parse(JSON.stringify(row)); })
}
const save = () => {
  axios.post('http://localhost:8090/goods/saveOrMod', form.value).then(res => {
    if(res.data.code === 200){
      ElMessage.success('保存成功');
      centerDialogVisible.value = false;
      loadPost();
    } else ElMessage.error('操作失败');
  })
}
const del = (id) => {
  axios.get('http://localhost:8090/goods/delete?id='+id).then(res => {
    if(res.data.code===200){ ElMessage.success('删除成功'); loadPost(); } else ElMessage.error('删除失败');
  })
}

// In/Out Stock
const inGoods = (row) => {
  inOutVisible.value = true;
  nextTick(() => { form1.value = { goodsId: row.id, goodsname: row.name, count: 1, remark: '', flag: 1 }; })
}
const outGoods = (row) => {
  inOutVisible.value = true;
  nextTick(() => { form1.value = { goodsId: row.id, goodsname: row.name, count: 1, remark: '', flag: 2 }; })
}
const saveInOut = () => {
  let url = form1.value.flag === 1 ? 'inStock' : 'outStock';
  const requestData = { ...form1.value, userId: user.id };
  axios.post(`http://localhost:8090/goods/${url}`, requestData).then(res => {
    if(res.data.code===200){
      ElMessage.success('操作成功');
      inOutVisible.value = false;
      loadPost();
    } else ElMessage.error(res.data.msg || '操作失败');
  })
}

// Cart Logic
const addCart = (row) => {
  cartForm.value = { 
    goodsId: row.id, name: row.name, price: row.price || 0, count: 1, stock: row.count 
  };
  cartDialogVisible.value = true;
}
const confirmAddCart = () => {
  axios.post('http://localhost:8090/cart/save', {
    goodsId: cartForm.value.goodsId, userId: user.id, count: cartForm.value.count
  }).then(res => {
    if(res.data.code===200){
      ElMessage.success('已成功加入购物车');
      cartDialogVisible.value = false;
    } else ElMessage.error(res.data.msg || '加入失败');
  })
}

onMounted(() => {
  checkGlobalSearch();
  loadStorage();
  loadGoodsType();
  loadPost();
})
</script>

<style scoped>
.goods-page {
  min-height: 100%;
  background-color: #f8fafc;
}

/* 1. Hero Header */
.goods-hero {
  position: relative;
  height: 260px;
  background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  margin-top: -24px;
}
.hero-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(circle at 10% 20%, rgba(99, 102, 241, 0.15), transparent 40%),
                    radial-gradient(circle at 90% 80%, rgba(139, 92, 246, 0.15), transparent 40%);
}
.hero-content { position: relative; z-index: 2; color: #fff; margin-bottom: 40px; }
.page-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; letter-spacing: 1px; }
.page-subtitle { color: rgba(255,255,255,0.7); font-size: 15px; }

/* 2. Filter Bar */
.filter-bar-wrapper {
  max-width: 1200px;
  margin: -60px auto 30px; /* Overlap Hero */
  padding: 0 24px;
  position: relative;
  z-index: 5;
}
.filter-bar {
  background: #fff;
  border-radius: 16px;
  padding: 12px 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}
.filter-left, .filter-right { display: flex; align-items: center; gap: 12px; }
.filter-item {
  background: #f8fafc;
  border-radius: 10px;
  display: flex; align-items: center;
  padding: 0 12px;
  height: 48px;
  transition: all 0.2s;
}
.filter-item:hover, .filter-item:focus-within { background: #f1f5f9; }
.search-icon { color: #94a3b8; margin-right: 8px; font-size: 18px; }
.custom-input {
  border: none; background: transparent; outline: none;
  font-size: 14px; width: 140px; color: #333;
}
.select-item { padding: 0; width: 140px; }
/* Element Select Override (deep) used in component style usually, but here scoped */
:deep(.el-input__wrapper) { box-shadow: none !important; background: transparent !important; }
.action-btn {
  height: 42px; border-radius: 10px; font-weight: 500;
  display: flex; align-items: center; gap: 6px;
}
.add-btn { margin-left: auto; }

/* 3. Goods Grid */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 60px;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}
.full-width-empty { grid-column: 1 / -1; }

/* Card */
.goods-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
  transition: all 0.3s ease;
  display: flex; flex-direction: column;
  border: 1px solid #f1f5f9;
  position: relative;
}
.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 30px rgba(0,0,0,0.08);
  border-color: #e2e8f0;
}

.card-image-section {
  height: 180px;
  background: #f8fafc;
  display: flex; align-items: center; justify-content: center;
  position: relative;
}
.goods-icon-wrapper {
  width: 80px; height: 80px; border-radius: 20px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}
.icon-purple { background: linear-gradient(135deg, #a78bfa, #8b5cf6); }
.icon-blue { background: linear-gradient(135deg, #60a5fa, #3b82f6); }
.icon-green { background: linear-gradient(135deg, #34d399, #10b981); }
.icon-orange { background: linear-gradient(135deg, #fbbf24, #f59e0b); }

.stock-badge {
  position: absolute; top: 12px; left: 12px;
  font-size: 11px; padding: 4px 10px; border-radius: 20px;
  font-weight: 600; color: #fff;
}
.low-stock { background: #ef4444; }
.normal-stock { background: #10b981; }

.hover-actions {
  position: absolute; bottom: 12px;
  display: flex; gap: 8px;
  opacity: 0; transform: translateY(10px);
  transition: all 0.3s;
}
.goods-card:hover .hover-actions { opacity: 1; transform: translateY(0); }
.icon-btn {
  width: 36px; height: 36px; border-radius: 50%; border: none;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: #fff; box-shadow: 0 4px 10px rgba(0,0,0,0.2);
  transition: transform 0.2s;
}
.icon-btn:hover { transform: scale(1.1); }
.in-btn { background: #3b82f6; }
.out-btn { background: #8b5cf6; }
.edit-btn { background: #f59e0b; }
.del-btn { background: #ef4444; }

.card-content { padding: 20px; flex: 1; display: flex; flex-direction: column; }
.goods-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; }
.goods-name { font-size: 16px; font-weight: 700; color: #1e293b; margin: 0; line-height: 1.4; flex: 1; padding-right: 8px; }
.storage-tag { font-size: 12px; color: #64748b; display: flex; align-items: center; gap: 4px; background: #f1f5f9; padding: 2px 8px; border-radius: 6px; }

.goods-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.category-tag { font-size: 12px; color: #6366f1; background: #e0e7ff; padding: 4px 10px; border-radius: 20px; display: flex; align-items: center; gap: 4px; }
.price-tag { font-size: 20px; color: #ef4444; font-weight: 700; }

.stock-info { margin-bottom: 12px; }
.stock-bar-bg { height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; margin-bottom: 4px; }
.stock-bar-fill { height: 100%; border-radius: 3px; }
.bg-red { background: #ef4444; } .bg-blue { background: #3b82f6; }
.stock-text { font-size: 12px; color: #94a3b8; }

.goods-remark { font-size: 13px; color: #94a3b8; margin: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.card-footer { padding: 16px 20px; border-top: 1px solid #f1f5f9; background: #f8fafc; }
.cart-btn { width: 100%; border-radius: 10px; }

/* Pagination */
.pagination-section { display: flex; justify-content: center; margin-top: 20px; }

/* Dialog specific */
.cart-preview { display: flex; gap: 16px; align-items: center; padding: 16px; background: #f8fafc; border-radius: 12px; }
.cart-icon { width: 50px; height: 50px; background: #fff; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #f59e0b; font-size: 24px; }
.cart-info h3 { margin: 0 0 4px; font-size: 16px; color: #1e293b; }
.cart-price { margin: 0; color: #ef4444; font-weight: 700; font-size: 18px; }
.total-price { font-size: 24px; color: #ef4444; font-weight: 800; font-family: sans-serif; }


/* Image Upload & Display */
.goods-real-img { width: 100%; height: 100%; display: block; transition: transform 0.3s; }
.goods-card:hover .goods-real-img { transform: scale(1.05); }

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: .2s;
  background-color: #fafafa;
}
.avatar-uploader .el-upload:hover { border-color: #3b82f6; }
.avatar-uploader-icon {
  font-size: 28px; color: #8c939d;
  width: 100px; height: 100px;
  text-align: center;
  display: flex !important; align-items: center; justify-content: center;
}
.avatar { width: 100px; height: 100px; display: block; object-fit: cover; }

</style>