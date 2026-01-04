<template>
  <div>
    <h3>我的购物车</h3>

    <!-- 1. 表格区域 -->
    <el-table :data="tableData" border style="width: 100%" show-summary :summary-method="getSummaries">

      <el-table-column prop="goodsName" label="物品名称" width="180">
        <template #default="scope">
          <el-tag size="large">{{ scope.row.goodsName }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="storageName" label="发货仓库" width="120" />

      <el-table-column prop="price" label="单价" width="120">
        <template #default="scope">
          <span style="color: red;">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="count" label="购买数量" width="150">
        <template #default="scope">
          <!-- 允许在购物车直接改数量，更像电商 -->
          <el-input-number v-model="scope.row.count" :min="1" size="small" @change="updateCount(scope.row)"></el-input-number>
        </template>
      </el-table-column>

      <el-table-column label="小计(总金额)" width="150">
        <template #default="scope">
          <!-- 自动计算：单价 * 数量 -->
          <span style="color: red; font-weight: bold;">
               ￥{{ (scope.row.price * scope.row.count).toFixed(2) }}
            </span>
        </template>
      </el-table-column>

      <el-table-column prop="createtime" label="添加时间" width="180">
        <template #default="scope">
          <el-icon><Clock /></el-icon>
          <span style="margin-left: 5px">{{ formatTime(scope.row.createtime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="success" size="small" @click="showConfirmDialog(scope.row)">结算(下单)</el-button>
          <el-button type="danger" size="small" @click="del(scope.row.id)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 2. 订单确认弹窗 -->
    <el-dialog v-model="confirmDialogVisible" title="确认订单" width="550px" :close-on-click-modal="false">
      <div class="order-confirm-container">
        <!-- 商品信息区域 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Goods /></el-icon>
            <span>商品信息</span>
          </div>
          <div class="info-card">
            <div class="info-row">
              <span class="label">商品名称：</span>
              <el-tag size="large" type="primary">{{ currentOrder.goodsName }}</el-tag>
            </div>
            <div class="info-row">
              <span class="label">发货仓库：</span>
              <span class="value">{{ currentOrder.storageName }}</span>
            </div>
            <div class="info-row">
              <span class="label">单价：</span>
              <span class="price">￥{{ currentOrder.price }}</span>
            </div>
            <div class="info-row">
              <span class="label">购买数量：</span>
              <span class="value">{{ currentOrder.count }} 件</span>
            </div>
            <div class="info-row total">
              <span class="label">应付金额：</span>
              <span class="total-price">￥{{ (currentOrder.price * currentOrder.count).toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- 收货地址区域 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Location /></el-icon>
            <span>收货信息</span>
          </div>
          <div v-if="addressList.length > 0" class="address-select">
            <el-radio-group v-model="selectedAddressId" class="address-radio-group">
              <el-radio v-for="addr in addressList" :key="addr.id" :value="addr.id" class="address-radio">
                <div class="address-item">
                  <div class="address-main">
                    <span class="linkman">{{ addr.linkman }}</span>
                    <span class="phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.isDefault === 'Y'" type="success" size="small">默认</el-tag>
                  </div>
                  <div class="address-detail">{{ addr.address }}</div>
                </div>
              </el-radio>
            </el-radio-group>
          </div>
          <el-empty v-else description="暂无收货地址，请先到个人中心添加收货地址" :image-size="60" />
        </div>
      </div>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedAddressId" @click="goToPayment">
          <el-icon><CreditCard /></el-icon>
          去支付
        </el-button>
      </template>
    </el-dialog>

    <!-- 3. 支付弹窗 -->
    <el-dialog v-model="paymentDialogVisible" title="支付订单" width="450px" :close-on-click-modal="false">
      <div class="payment-container">
        <div class="payment-amount">
          <div class="amount-label">支付金额</div>
          <div class="amount-value">￥{{ (currentOrder.price * currentOrder.count).toFixed(2) }}</div>
        </div>

        <div class="payment-method">
          <div class="method-title">选择支付方式</div>
          <el-radio-group v-model="paymentMethod" class="method-group">
            <el-radio value="alipay" class="method-item">
              <div class="method-content">
                <span class="method-icon alipay-icon">支</span>
                <span>支付宝</span>
              </div>
            </el-radio>
            <el-radio value="wechat" class="method-item">
              <div class="method-content">
                <span class="method-icon wechat-icon">微</span>
                <span>微信支付</span>
              </div>
            </el-radio>
            <el-radio value="balance" class="method-item">
              <div class="method-content">
                <span class="method-icon balance-icon">余</span>
                <span>账户余额</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <div class="delivery-info">
          <div class="delivery-title">配送至</div>
          <div class="delivery-address">
            <span class="linkman">{{ selectedAddress?.linkman }}</span>
            <span class="phone">{{ selectedAddress?.phone }}</span>
            <div class="address">{{ selectedAddress?.address }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="paymentLoading" @click="confirmPayment">
          <el-icon><Check /></el-icon>
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Clock, Goods, Location, CreditCard, Check } from '@element-plus/icons-vue';

const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);

// 订单确认弹窗相关
const confirmDialogVisible = ref(false);
const currentOrder = ref({});
const addressList = ref([]);
const selectedAddressId = ref(null);

// 支付弹窗相关
const paymentDialogVisible = ref(false);
const paymentMethod = ref('alipay');
const paymentLoading = ref(false);

// 计算选中的地址对象
const selectedAddress = computed(() => {
  return addressList.value.find(addr => addr.id === selectedAddressId.value);
});

const load = () => {
  axios.get('http://localhost:8090/cart/list?userId=' + user.id).then(res => {
    tableData.value = res.data.data;
  })
}

// 加载用户收货地址
const loadAddress = () => {
  axios.get(`http://localhost:8090/address/list?userId=${user.id}`).then(res => {
    addressList.value = res.data.data || [];
    // 默认选中默认地址
    const defaultAddr = addressList.value.find(addr => addr.isDefault === 'Y');
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id;
    } else if (addressList.value.length > 0) {
      selectedAddressId.value = addressList.value[0].id;
    }
  });
}

// 格式化时间：把 2025-12-31T12:00:00 变成 2025-12-31 12:00
const formatTime = (timeStr) => {
  if(!timeStr) return '';
  return timeStr.replace('T', ' ').substring(0, 19);
}

// 显示订单确认弹窗
const showConfirmDialog = (row) => {
  currentOrder.value = { ...row };
  loadAddress();
  confirmDialogVisible.value = true;
}

// 跳转到支付界面
const goToPayment = () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请先选择收货地址');
    return;
  }
  confirmDialogVisible.value = false;
  paymentDialogVisible.value = true;
}

// 确认支付
const confirmPayment = () => {
  paymentLoading.value = true;
  
  axios.post('http://localhost:8090/goods/outStock', {
    goodsId: currentOrder.value.goodsId,
    count: currentOrder.value.count,
    remark: `用户下单购买 - 收货人: ${selectedAddress.value?.linkman}, 地址: ${selectedAddress.value?.address}, 支付方式: ${getPaymentMethodName(paymentMethod.value)}`,
    userId: user.id
  }).then(res => {
    paymentLoading.value = false;
    if(res.data.code === 200){
      ElMessage.success('支付成功！订单已提交');
      paymentDialogVisible.value = false;
      del(currentOrder.value.id);
    } else {
      ElMessage.error(res.data.msg || '支付失败');
    }
  }).catch(() => {
    paymentLoading.value = false;
    ElMessage.error('支付请求失败，请稍后重试');
  })
}

// 获取支付方式名称
const getPaymentMethodName = (method) => {
  const names = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'balance': '账户余额'
  };
  return names[method] || method;
}

// 移除
const del = (id) => {
  axios.get('http://localhost:8090/cart/delete?id='+id).then(res=>{
    load();
  })
}

// 更新数量 (可选功能：如果想把修改后的数量同步回数据库，需要后端加update接口)
// 这里暂时只做前端展示变化
const updateCount = (row) => {
  // 实际项目中这里应该调用 axios.post('/cart/update', row)
  console.log("数量变为：", row.count);
}

// 底部合计逻辑 (Element Plus 表格自带功能)
const getSummaries = (param) => {
  const { columns, data } = param;
  const sums = [];
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '总价合计';
      return;
    }
    // 只计算"小计"这一列
    if (column.label === '小计(总金额)') {
      const values = data.map(item => item.price * item.count);
      const total = values.reduce((prev, curr) => {
        const value = Number(curr);
        if (!isNaN(value)) {
          return prev + curr;
        } else {
          return prev;
        }
      }, 0);
      sums[index] = '￥' + total.toFixed(2);
    } else {
      sums[index] = '';
    }
  });
  return sums;
}

onMounted(() => { load(); })
</script>

<style scoped>
/* 订单确认弹窗样式 */
.order-confirm-container {
  padding: 10px 0;
}

.section {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.info-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  width: 80px;
  color: #606266;
  font-size: 14px;
}

.info-row .value {
  color: #303133;
  font-size: 14px;
}

.info-row .price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 500;
}

.info-row.total {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #dcdfe6;
}

.info-row .total-price {
  color: #f56c6c;
  font-size: 22px;
  font-weight: bold;
}

/* 地址选择样式 */
.address-select {
  max-height: 200px;
  overflow-y: auto;
}

.address-radio-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.address-radio {
  width: 100%;
  height: auto !important;
  margin-right: 0 !important;
}

.address-radio :deep(.el-radio__label) {
  width: 100%;
  padding-left: 10px;
}

.address-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  width: 100%;
}

.address-main {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.address-main .linkman {
  font-weight: 600;
  color: #303133;
}

.address-main .phone {
  color: #606266;
}

.address-detail {
  color: #909399;
  font-size: 13px;
  line-height: 1.5;
}

/* 支付弹窗样式 */
.payment-container {
  padding: 10px 0;
}

.payment-amount {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 20px;
}

.amount-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-bottom: 8px;
}

.amount-value {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
}

.payment-method {
  margin-bottom: 20px;
}

.method-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.method-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.method-item {
  height: auto !important;
  margin-right: 0 !important;
}

.method-item :deep(.el-radio__label) {
  padding-left: 10px;
}

.method-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.method-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.alipay-icon {
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
}

.wechat-icon {
  background: linear-gradient(135deg, #07c160 0%, #06ae56 100%);
}

.balance-icon {
  background: linear-gradient(135deg, #ff9500 0%, #ff6a00 100%);
}

.delivery-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.delivery-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.delivery-address {
  color: #303133;
}

.delivery-address .linkman {
  font-weight: 600;
  margin-right: 10px;
}

.delivery-address .phone {
  color: #606266;
}

.delivery-address .address {
  margin-top: 5px;
  color: #606266;
  font-size: 13px;
}
</style>