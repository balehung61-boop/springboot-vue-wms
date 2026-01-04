<template>
  <div class="cart-page">
    <!-- 1. Hero Header -->
    <div class="cart-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="page-title">我的购物车</h1>
        <p class="page-subtitle">管理您的购物清单，快捷结算心仪好物</p>
      </div>
    </div>

    <!-- 2. Main Layout: Left List + Right Summary -->
    <div class="cart-container">
      
      <!-- Left: Items List -->
      <div class="cart-items-section">
        <div class="section-header">
           <h2>全部商品 <span class="item-count">({{ tableData.length }})</span></h2>
        </div>

        <transition-group name="list">
          <div class="cart-card" v-for="item in tableData" :key="item.id">
            <!-- Icon/Image -->
            <div class="item-image">
              <el-icon><Goods /></el-icon>
            </div>

            <!-- Content -->
            <div class="item-content">
               <div class="item-main">
                 <h3 class="item-name">{{ item.goodsName }}</h3>
                 <div class="item-tags">
                    <span class="warehouse-tag">
                      <el-icon><OfficeBuilding /></el-icon> {{ item.storageName }}
                    </span>
                    <span class="time-tag">
                      <el-icon><Clock /></el-icon> {{ formatTime(item.createtime) }}
                    </span>
                 </div>
               </div>

               <div class="item-controls">
                 <div class="price-box">
                    <div class="unit-price">单价: ￥{{ item.price }}</div>
                    <div class="total-price">￥{{ (item.price * item.count).toFixed(2) }}</div>
                 </div>

                 <div class="quantity-box">
                    <el-input-number 
                      v-model="item.count" 
                      :min="1" 
                      size="small" 
                      @change="updateCount(item)" 
                      class="custom-stepper"
                    />
                 </div>
               </div>
            </div>

            <!-- Actions -->
            <div class="item-actions">
              <el-button type="primary" class="buy-btn" @click="showConfirmDialog(item)">
                立即下单
              </el-button>
              <el-tooltip content="移出购物车" placement="top">
                <button class="remove-btn" @click="del(item.id)">
                  <el-icon><Delete /></el-icon>
                </button>
              </el-tooltip>
            </div>
          </div>
        </transition-group>

        <!-- Empty State -->
        <el-empty v-if="tableData.length === 0" description="购物车空空如也，快去选购吧~" :image-size="160">
           <el-button type="primary" @click="$router.push('/goods')">去逛逛</el-button>
        </el-empty>
      </div>

      <!-- Right: Summary Sidebar (Sticky) -->
      <div class="cart-sidebar" v-if="tableData.length > 0">
        <div class="summary-card">
           <h3 class="summary-title">账单概览</h3>
           
           <div class="summary-row">
             <span>商品数量</span>
             <span>{{ tableData.length }} 件</span>
           </div>
           
           <div class="summary-row total-row">
             <span>预估总价</span>
             <span class="final-price">￥{{ overallTotal }}</span>
           </div>
           
           <div class="summary-tip">
             <el-icon><InfoFilled /></el-icon> 请点击左侧商品“立即下单”进行单独结算
           </div>
        </div>
      </div>

    </div>

    <!-- 2. 订单确认弹窗 (Modernized) -->
    <el-dialog v-model="confirmDialogVisible" title="确认订单" width="600px" class="modern-dialog" align-center>
      <div class="order-confirm-container">
        <!-- Merged Info Card -->
        <div class="checkout-card">
           <div class="goods-summary-row">
              <div class="gs-icon"><el-icon><Goods /></el-icon></div>
              <div class="gs-info">
                 <h4>{{ currentOrder.goodsName }}</h4>
                 <p>发货仓: {{ currentOrder.storageName }}</p>
              </div>
              <div class="gs-price">
                 <small>{{ currentOrder.count }} x ￥{{ currentOrder.price }}</small>
                 <div class="gs-total">￥{{ (currentOrder.price * currentOrder.count).toFixed(2) }}</div>
              </div>
           </div>
        </div>

        <!-- Address Selection -->
        <div class="checkout-section">
           <div class="section-header-row">
             <span class="sec-title"><el-icon><Location /></el-icon> 收货地址</span>
             <el-button link type="primary" @click="openAddressDialog(null)">+ 新增地址</el-button>
           </div>
           
           <div class="address-grid" v-if="addressList.length > 0">
              <div 
                v-for="addr in addressList" 
                :key="addr.id" 
                class="address-card"
                :class="{ 'selected': selectedAddressId === addr.id }"
                @click="selectedAddressId = addr.id"
              >
                 <div class="addr-header">
                    <span class="addr-name">{{ addr.linkman }}</span>
                    <span class="addr-phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.isDefault === 'Y'" size="small" type="success" effect="plain">默认</el-tag>
                 </div>
                 <p class="addr-text">{{ addr.address }}</p>
                 <button class="addr-edit-btn" @click.stop="openAddressDialog(addr)"><el-icon><Edit /></el-icon></button>
              </div>
           </div>
           <el-empty v-else description="暂无地址，请先添加" :image-size="60" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
           <el-button @click="confirmDialogVisible = false">取消</el-button>
           <el-button type="primary" :disabled="!selectedAddressId" @click="goToPayment" size="large" class="pay-btn">
             去支付 ￥{{ (currentOrder.price * currentOrder.count).toFixed(2) }}
           </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 3. 支付弹窗 -->
    <el-dialog v-model="paymentDialogVisible" title="收银台" width="480px" class="modern-dialog" align-center>
      <div class="payment-wrapper">
        <div class="pay-amount-box">
           <p>应付金额</p>
           <h2>￥{{ (currentOrder.price * currentOrder.count).toFixed(2) }}</h2>
        </div>

        <div class="pay-methods">
           <label class="pay-item" :class="{ active: paymentMethod === 'alipay' }">
             <input type="radio" value="alipay" v-model="paymentMethod" hidden>
             <div class="pay-icon alipay">支</div>
             <span>支付宝</span>
             <el-icon class="check-mark" v-if="paymentMethod === 'alipay'"><Select /></el-icon>
           </label>
           <label class="pay-item" :class="{ active: paymentMethod === 'wechat' }">
             <input type="radio" value="wechat" v-model="paymentMethod" hidden>
             <div class="pay-icon wechat">微</div>
             <span>微信支付</span>
             <el-icon class="check-mark" v-if="paymentMethod === 'wechat'"><Select /></el-icon>
           </label>
           <label class="pay-item" :class="{ active: paymentMethod === 'balance' }">
             <input type="radio" value="balance" v-model="paymentMethod" hidden>
             <div class="pay-icon balance">余</div>
             <span>余额支付</span>
             <el-icon class="check-mark" v-if="paymentMethod === 'balance'"><Select /></el-icon>
           </label>
        </div>
      </div>
      <template #footer>
         <el-button type="primary" :loading="paymentLoading" @click="confirmPayment" size="large" style="width: 100%;">
            立即支付
         </el-button>
      </template>
    </el-dialog>

    <!-- 4. 地址编辑弹窗 -->
    <el-dialog v-model="addressDialogVisible" :title="isEditAddress ? '编辑收货地址' : '新建收货地址'" width="420px" class="modern-dialog" append-to-body>
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="80px" class="custom-form">
        <el-form-item label="联系人" prop="linkman">
          <el-input v-model="addressForm.linkman" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="手机号码"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input type="textarea" v-model="addressForm.address" placeholder="省/市/区/街道" rows="3"></el-input>
        </el-form-item>
        <el-form-item label="默认">
          <el-switch v-model="addressForm.isDefault" active-value="Y" inactive-value="N"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addressSaving" @click="saveAddressForm">保存地址</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { 
  Clock, Goods, Location, CreditCard, Select, Plus, Edit, 
  Delete, OfficeBuilding, InfoFilled 
} from '@element-plus/icons-vue';

const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);

// 弹窗状态
const confirmDialogVisible = ref(false);
const currentOrder = ref({});
const addressList = ref([]);
const selectedAddressId = ref(null);

const paymentDialogVisible = ref(false);
const paymentMethod = ref('alipay');
const paymentLoading = ref(false);

const addressDialogVisible = ref(false);
const addressFormRef = ref(null);
const isEditAddress = ref(false);
const addressSaving = ref(false);
const addressForm = ref({ id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N' });

const addressRules = {
  linkman: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
};

const selectedAddress = computed(() => addressList.value.find(addr => addr.id === selectedAddressId.value));

// 全局总价计算
const overallTotal = computed(() => {
  if (!tableData.value.length) return '0.00';
  const total = tableData.value.reduce((sum, item) => sum + (item.price * item.count), 0);
  return total.toFixed(2);
});

const load = () => {
  axios.get('http://localhost:8090/cart/list?userId=' + user.id).then(res => {
    tableData.value = res.data.data;
  })
}

// 格式化时间
const formatTime = (timeStr) => {
  if(!timeStr) return '';
  return timeStr.replace('T', ' ').substring(0, 16);
}

// 加载地址
const loadAddress = () => {
  axios.get(`http://localhost:8090/address/list?userId=${user.id}`).then(res => {
    addressList.value = res.data.data || [];
    const defaultAddr = addressList.value.find(addr => addr.isDefault === 'Y');
    if (defaultAddr) selectedAddressId.value = defaultAddr.id;
    else if (addressList.value.length > 0) selectedAddressId.value = addressList.value[0].id;
  });
}

// 订单流程
const showConfirmDialog = (row) => {
  currentOrder.value = { ...row };
  loadAddress();
  confirmDialogVisible.value = true;
}

const goToPayment = () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请先选择收货地址');
    return;
  }
  confirmDialogVisible.value = false;
  paymentDialogVisible.value = true;
}

const confirmPayment = () => {
  paymentLoading.value = true;
  axios.post('http://localhost:8090/goods/outStock', {
    goodsId: currentOrder.value.goodsId,
    count: currentOrder.value.count,
    remark: `下单 - 收货: ${selectedAddress.value?.linkman}, 地址: ${selectedAddress.value?.address}, 方式: ${paymentMethod.value}`,
    userId: user.id
  }).then(res => {
    paymentLoading.value = false;
    if(res.data.code === 200){
      ElMessage.success('支付成功！');
      paymentDialogVisible.value = false;
      del(currentOrder.value.id);
    } else {
      ElMessage.error(res.data.msg || '支付失败');
    }
  }).catch(() => {
     paymentLoading.value = false;
     ElMessage.error('网络错误');
  })
}

const del = (id) => {
  axios.get('http://localhost:8090/cart/delete?id='+id).then(res=>{
    load();
    ElMessage.success('已移除');
  })
}

const updateCount = (row) => {
  // 占位，需后端接口支持更新
  console.log("update:", row.count);
}

// 地址弹窗
const openAddressDialog = (addr) => {
  if (addr) {
    isEditAddress.value = true;
    addressForm.value = { ...addr };
  } else {
    isEditAddress.value = false;
    addressForm.value = { id: '', userId: user.id, linkman: '', phone: '', address: '', isDefault: 'N' };
  }
  addressDialogVisible.value = true;
}

const saveAddressForm = async () => {
  if (!addressFormRef.value) return;
  try {
    await addressFormRef.value.validate();
    addressSaving.value = true;
    const res = await axios.post('http://localhost:8090/address/saveOrMod', addressForm.value);
    addressSaving.value = false;
    if (res.data.code === 200) {
      ElMessage.success(isEditAddress.value ? '已修改' : '已添加');
      addressDialogVisible.value = false;
      loadAddress();
    } else ElMessage.error(res.data.msg || '保存失败');
  } catch(e) { addressSaving.value = false; }
}

onMounted(() => { load(); })
</script>

<style scoped>
.cart-page {
  min-height: 100%;
  background-color: #f8fafc;
}

/* 1. Hero Header */
.cart-hero {
  position: relative;
  height: 240px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  display: flex; align-items: center; justify-content: center;
  text-align: center;
  width: 100%; margin-top: -24px;
}
.hero-overlay {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(circle at 50% 50%, rgba(99, 102, 241, 0.1), transparent 50%);
}
.hero-content { position: relative; z-index: 2; color: #fff; margin-bottom: 20px; }
.page-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; }
.page-subtitle { color: #94a3b8; font-size: 15px; }

/* 2. Layout */
.cart-container {
  max-width: 1200px;
  margin: -60px auto 40px; /* Overlap */
  padding: 0 24px;
  display: flex;
  gap: 24px;
  position: relative;
  z-index: 5;
}

.cart-items-section { flex: 1; }
.section-header h2 { font-size: 20px; color: #1e293b; margin-bottom: 16px; font-weight: 700; }
.item-count { font-size: 16px; color: #64748b; font-weight: 400; }

.cart-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}
.cart-card:hover { transform: translateY(-2px); box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1); }

.item-image {
  width: 80px; height: 80px;
  background: #f1f5f9;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #94a3b8; font-size: 32px;
}

.item-content { flex: 1; display: flex; justify-content: space-between; align-items: center; }
.item-main { display: flex; flex-direction: column; gap: 8px; }
.item-name { font-size: 18px; color: #0f172a; margin: 0; font-weight: 600; }
.item-tags { display: flex; gap: 10px; }
.warehouse-tag, .time-tag {
  font-size: 12px; color: #64748b; background: #f8fafc;
  padding: 4px 8px; border-radius: 6px;
  display: flex; align-items: center; gap: 4px;
}

.item-controls { display: flex; align-items: center; gap: 24px; }
.price-box { text-align: right; }
.unit-price { font-size: 13px; color: #94a3b8; }
.total-price { font-size: 18px; color: #0f172a; font-weight: 700; }
.quantity-box :deep(.el-input-number) { width: 100px; }

.item-actions { display: flex; flex-direction: column; gap: 8px; align-items: flex-end; }
.buy-btn { border-radius: 20px; padding: 8px 20px; }
.remove-btn {
  background: transparent; border: none; color: #ef4444; 
  cursor: pointer; padding: 4px; font-size: 18px;
  opacity: 0.6; transition: 0.2s;
}
.remove-btn:hover { opacity: 1; transform: scale(1.1); }

/* Sidebar */
.cart-sidebar { width: 320px; }
.summary-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  position: sticky; top: 24px;
}
.summary-title { font-size: 18px; margin: 0 0 20px; }
.summary-row { display: flex; justify-content: space-between; margin-bottom: 12px; color: #64748b; font-size: 14px; }
.total-row {
  margin-top: 16px; padding-top: 16px;
  border-top: 1px dashed #e2e8f0;
  display: flex; justify-content: space-between; align-items: flex-end;
}
.final-price { font-size: 28px; color: #0f172a; font-weight: 800; }
.summary-tip {
  margin-top: 20px; background: #eff6ff;
  color: #3b82f6; font-size: 12px; padding: 10px;
  border-radius: 8px; display: flex; gap: 6px;
}

/* Modal Internal */
.checkout-card { background: #f8fafc; padding: 16px; border-radius: 12px; margin-bottom: 24px; }
.goods-summary-row { display: flex; align-items: center; gap: 16px; }
.gs-icon { width: 48px; height: 48px; background: #fff; border-radius: 8px; display: flex; justify-content: center; align-items: center; color: #64748b; }
.gs-info { flex: 1; }
.gs-info h4 { margin: 0 0 4px; font-size: 15px; }
.gs-info p { color: #64748b; font-size: 12px; margin: 0; }
.gs-price { text-align: right; }
.gs-total { font-weight: 700; font-size: 16px; color: #0f172a; }

.section-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.sec-title { font-weight: 600; display: flex; align-items: center; gap: 6px; }
.address-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; max-height: 240px; overflow-y: auto; }
.address-card {
  border: 1px solid #e2e8f0; border-radius: 10px; padding: 12px;
  cursor: pointer; position: relative; transition: all 0.2s;
}
.address-card:hover { border-color: #94a3b8; }
.address-card.selected { border-color: #3b82f6; background: #eff6ff; box-shadow: 0 0 0 1px #3b82f6; }
.addr-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.addr-name { font-weight: 600; font-size: 14px; }
.addr-text { font-size: 12px; color: #64748b; margin: 0; line-height: 1.4; }
.addr-edit-btn { position: absolute; top: 8px; right: 8px; border: none; background: transparent; color: #94a3b8; cursor: pointer; }

/* Pay Methods */
.payment-wrapper { padding: 12px 0; }
.pay-amount-box { text-align: center; margin-bottom: 24px; }
.pay-amount-box p { color: #64748b; margin: 0 0 4px; font-size: 13px; }
.pay-amount-box h2 { font-size: 36px; color: #0f172a; margin: 0; font-weight: 800; }

.pay-methods { display: flex; flex-direction: column; gap: 12px; }
.pay-item {
  display: flex; align-items: center; gap: 12px;
  padding: 16px; border: 1px solid #e2e8f0; border-radius: 12px;
  cursor: pointer; transition: 0.2s;
}
.pay-item:hover { border-color: #cbd5e1; }
.pay-item.active { border-color: #3b82f6; background: #f0f9ff; }
.pay-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; justify-content: center; align-items: center; color: #fff; font-weight: bold; font-size: 14px; }
.alipay { background: #1677ff; } .wechat { background: #07c160; } .balance { background: #f59e0b; }
.check-mark { margin-left: auto; color: #3b82f6; font-weight: bold; }

</style>