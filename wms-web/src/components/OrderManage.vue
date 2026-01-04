<template>
  <div class="order-page">
    <!-- 1. Hero Header -->
    <div class="order-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="page-title">我的历史订单</h1>
        <p class="page-subtitle">查看所有历史交易记录，追踪您的消费足迹</p>
      </div>
    </div>

    <!-- 2. Main Content -->
    <div class="order-container">
      
      <div class="timeline-header">
         <h2>订单时间轴 <span class="count" v-if="tableData.length">({{ tableData.length }})</span></h2>
      </div>

      <transition-group name="list" tag="div" class="order-list">
        <div class="order-card" v-for="(item, index) in tableData" :key="index">
          
          <!-- Left: Date Node -->
          <div class="order-node">
             <div class="node-dot"></div>
             <div class="node-line" v-if="index !== tableData.length - 1"></div>
          </div>

          <!-- Right: Content -->
          <div class="order-content-box">
             <div class="order-header">
                <div class="order-time">
                  <el-icon><Calendar /></el-icon> {{ formatTime(item.createtime) }}
                </div>
                <div class="order-status">
                   <el-tag type="success" effect="dark" size="small">已完成</el-tag>
                </div>
             </div>

             <div class="order-body">
                <div class="product-info">
                   <div class="product-icon">
                      <el-icon><Goods /></el-icon>
                   </div>
                   <div class="product-text">
                      <h3 class="product-name">{{ item.goodsname }}</h3>
                      <span class="product-count">x {{ Math.abs(item.count) }}</span>
                   </div>
                </div>
                
                <!-- Parsed Details from Remark -->
                <div class="order-details" v-if="parseRemark(item.remark)">
                   <div class="detail-item" v-if="parsedData(item.remark).linkman">
                      <el-icon><User /></el-icon>
                      <span>{{ parsedData(item.remark).linkman }}</span>
                   </div>
                   <div class="detail-item" v-if="parsedData(item.remark).address">
                      <el-icon><LocationInformation /></el-icon>
                      <span>{{ parsedData(item.remark).address }}</span>
                   </div>
                   <div class="detail-item" v-if="parsedData(item.remark).payMethod">
                      <el-icon><CreditCard /></el-icon>
                      <span>{{ parsedData(item.remark).payMethod }}</span>
                   </div>
                </div>
                <!-- Fallback if parse fails -->
                <div class="order-raw-remark" v-else>
                   {{ item.remark }}
                </div>
             </div>
          </div>

        </div>
      </transition-group>

      <el-empty v-if="tableData.length === 0" description="暂无历史订单记录" :image-size="160"></el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { 
  Calendar, Goods, User, LocationInformation, CreditCard 
} from '@element-plus/icons-vue';

const user = JSON.parse(sessionStorage.getItem('user'));
const tableData = ref([]);

// 格式化时间
const formatTime = (timeStr) => {
  if(!timeStr) return '';
  return timeStr.replace('T', ' ');
}

// 解析备注信息的辅助函数
// 备注通常格式: "用户下单购买 - 收货人: xxx, 地址: xxx, 支付方式: xxx"
const parseRemark = (remark) => {
  if (!remark || !remark.includes('收货人')) return null;
  return true;
}

const parsedData = (remark) => {
  const res = { linkman: '', address: '', payMethod: '' };
  try {
    // 简单解析逻辑
    if (remark.includes('收货人:')) {
      const p1 = remark.split('收货人:')[1];
      if (p1) {
        const p2 = p1.split(',');
        res.linkman = p2[0].trim();
      }
    }
    if (remark.includes('地址:')) {
      const p1 = remark.split('地址:')[1];
      if (p1) {
        const p2 = p1.split(','); // 假设后面还有逗号分隔，或者是结尾
        // 支付方式通常是最后一个
        const addrPart = p2[0]; 
        // 如果地址里有逗号可能会切断，这里做个简单宽容处理
        // 更严谨可以用正则，这里配合CartManage生成的格式
        // Cart生成格式: `... 地址: ${address}, 支付方式...`
        res.address = remark.match(/地址:\s*(.*?),\s*支付方式/)?.[1] || addrPart.trim();
      }
    }
    if (remark.includes('支付方式:')) {
       res.payMethod = remark.split('支付方式:')[1].trim();
    }
  } catch (e) {
    console.error('Parse remark error', e);
  }
  return res;
}

onMounted(() => {
  // 复用 listPage 接口
  axios.post('http://localhost:8090/record/listPage', {
    pageNum: 1, pageSize: 100,
    param: { userId: user.id } 
  }).then(res => {
    if (res.data.code === 200) {
       // 按时间倒序
       tableData.value = res.data.data.sort((a,b) => new Date(b.createtime) - new Date(a.createtime));
    }
  })
})
</script>

<style scoped>
.order-page {
  min-height: 100%;
  background-color: #f8fafc;
}

/* 1. Hero Header */
.order-hero {
  position: relative;
  height: 220px;
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  display: flex; align-items: center; justify-content: center;
  text-align: center;
  width: 100%; margin-top: -24px;
}
.hero-overlay {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background-image: linear-gradient(45deg, rgba(255,255,255,0.05) 25%, transparent 25%, transparent 50%, rgba(255,255,255,0.05) 50%, rgba(255,255,255,0.05) 75%, transparent 75%, transparent);
  background-size: 20px 20px;
}
.hero-content { position: relative; z-index: 2; color: #fff; margin-bottom: 20px; }
.page-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; }
.page-subtitle { color: #cbd5e1; font-size: 15px; }

/* 2. Container */
.order-container {
  max-width: 900px;
  margin: -50px auto 40px;
  padding: 0 24px;
  position: relative; z-index: 5;
}

.timeline-header {
  margin-bottom: 24px;
  padding-left: 16px;
}
.timeline-header h2 { font-size: 20px; color: #fff; margin: 0; display: flex; align-items: center; gap: 8px; text-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.count { font-size: 14px; opacity: 0.8; font-weight: 400; }

/* 3. List & Cards */
.order-list {
  display: flex; flex-direction: column; gap: 0;
}

.order-card {
  display: flex;
  gap: 20px;
  position: relative;
  padding-bottom: 32px;
}

/* Left Node */
.order-node {
  display: flex; flex-direction: column; align-items: center; width: 24px; flex-shrink: 0;
}
.node-dot {
  width: 14px; height: 14px; border-radius: 50%;
  background: #fff; border: 3px solid #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  margin-top: 6px; z-index: 2;
}
.node-line {
  flex: 1; width: 2px; background: #e2e8f0;
  margin-top: 4px; z-index: 1;
}

/* Right Content */
.order-content-box {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
  border: 1px solid #f1f5f9;
}
.order-content-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.order-header {
  display: flex; justify-content: space-between; align-items: center;
  padding-bottom: 12px; border-bottom: 1px dashed #e2e8f0; margin-bottom: 16px;
}
.order-time { font-size: 14px; color: #64748b; display: flex; align-items: center; gap: 6px; }

.order-body { display: flex; flex-direction: column; gap: 16px; }

.product-info { display: flex; align-items: center; gap: 16px; }
.product-icon {
  width: 48px; height: 48px; background: #eff6ff;
  border-radius: 12px; display: flex; align-items: center; justify-content: center;
  color: #3b82f6;
}
.product-text { display: flex; flex-direction: column; }
.product-name { margin: 0 0 4px; font-size: 16px; color: #1e293b; font-weight: 600; }
.product-count { font-size: 13px; color: #64748b; }

.order-details {
  background: #f8fafc; border-radius: 8px; padding: 12px;
  display: flex; flex-wrap: wrap; gap: 16px; row-gap: 8px;
}
.detail-item {
  display: flex; align-items: center; gap: 6px; font-size: 13px; color: #475569;
}
.detail-item .el-icon { color: #94a3b8; }

.order-raw-remark { font-size: 13px; color: #64748b; }

</style>