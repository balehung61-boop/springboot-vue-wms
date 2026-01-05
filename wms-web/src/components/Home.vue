<template>
  <div class="home-page">
    <!-- 1. 轮播图 Hero 区域 -->
    <div class="carousel-wrapper">
      <el-carousel trigger="click" height="480px" :interval="5000" arrow="always">
        <!-- Slide 1: 网站目标 -->
        <el-carousel-item>
          <div class="carousel-slide slide-1">
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h1 class="slide-title">WMS 智能仓储管理系统</h1>
              <p class="slide-subtitle">目标：打造全球领先的数字化库存管理平台，让仓储更简单</p>
            </div>
          </div>
        </el-carousel-item>
        <!-- Slide 2: 核心功能 -->
        <el-carousel-item>
          <div class="carousel-slide slide-2">
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h1 class="slide-title">实时监控 · 精准调度</h1>
              <p class="slide-subtitle">功能：出入库全流程追踪，多维度数据报表，库存预警零延迟</p>
            </div>
          </div>
        </el-carousel-item>
        <!-- Slide 3: 业务范围 -->
        <el-carousel-item>
          <div class="carousel-slide slide-3">
            <div class="slide-overlay"></div>
            <div class="slide-content">
              <h1 class="slide-title">全渠道业务支撑</h1>
              <p class="slide-subtitle">业务：支持电商、冷链、保税等多业态仓储需求，助力企业降本增效</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 悬浮搜索组件 (固定在轮播图之上) -->
      <div class="hero-float-content">
        <div class="hero-search">
          <el-icon class="search-icon"><Search /></el-icon>
          <input 
            type="text" 
            v-model="searchKeyword"
            class="search-input"
            placeholder="输入商品名称、仓库等关键词搜索..."
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <div class="quick-tags">
          <span class="quick-tag" @click="$router.push('/Storage')">仓库管理</span>
          <span class="quick-tag" @click="$router.push('/Goods')">商品管理</span>
          <span class="quick-tag" v-if="user?.roleId === 2" @click="$router.push('/Cart')">购物车</span>
          <span class="quick-tag" @click="$router.push('/Order')">订单查询</span>
        </div>
      </div>
    </div>

    <!-- 2. 网站介绍文字区域 (新增) -->
    <div class="intro-section">
      <div class="intro-header">
        <h2 class="intro-title">平台优势</h2>
        <div class="intro-divider"></div>
        <p class="intro-desc">
          我们致力于为企业提供专业、稳定、高效的仓储管理解决方案。
          <br>通过先进的技术架构与人性化的交互设计，重塑您的库存管理体验。
        </p>
      </div>
      <div class="intro-grid">
        <div class="intro-card">
          <div class="intro-icon"><el-icon><DataBoard /></el-icon></div>
          <h3 class="intro-card-title">数据可视化</h3>
          <p class="intro-card-text">集成ECharts图表引擎，将复杂的库存数据转化为直观的动态看板，决策更敏捷。</p>
        </div>
        <div class="intro-card">
          <div class="intro-icon"><el-icon><Lock /></el-icon></div>
          <h3 class="intro-card-title">安全可靠</h3>
          <p class="intro-card-text">基于Spring Boot构建的稳健后端与严格的权限控制体系，全方位保障您的数据资产安全。</p>
        </div>
        <div class="intro-card">
          <div class="intro-icon"><el-icon><Cpu /></el-icon></div>
          <h3 class="intro-card-title">智能自动化</h3>
          <p class="intro-card-text">支持库存上下限预警、自动生成出入库单据，减少人工干预，大幅提升作业效率。</p>
        </div>
      </div>
    </div>

    <!-- 3. 为什么选择我们 (新增动画版块) -->
    <div class="why-section">
      <div class="why-header">
        <h2 class="why-title">为什么选择 WMS?</h2>
        <p class="why-subtitle">功能强大! 完全免费! 7*24小时一对一服务</p>
      </div>
      
      <div class="why-grid">
        <!-- Card 1 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 0 }" @click="activeWhyCard = 0">
          <div class="why-icon"><el-icon><Money /></el-icon></div>
          <div class="why-content">
            <h3>完全免费</h3>
            <p>注册免费！年费免！维护费全免！成本压力一键清空，省钱省力更省心！</p>
          </div>
        </div>

        <!-- Card 2 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 1 }" @click="activeWhyCard = 1">
          <div class="why-icon" v-if="activeWhyCard !== 1"><el-icon><Place /></el-icon></div>
          <div class="why-content">
            <h3>契合本土</h3>
            <p>支持中、英、日等多国语言；以国内仓储业务场景为中心，操作贴近本土员工使用习惯。</p>
          </div>
        </div>

        <!-- Card 3 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 2 }" @click="activeWhyCard = 2">
          <div class="why-icon"><el-icon><Timer /></el-icon></div>
          <div class="why-content">
            <h3>迭代迅速</h3>
            <p>实时响应客户需求，以高效迭代的节奏按时交付新功能，做到每周都有新功能上线。</p>
          </div>
        </div>

        <!-- Card 4 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 3 }" @click="activeWhyCard = 3">
          <div class="why-icon"><el-icon><Medal /></el-icon></div>
          <div class="why-content">
            <h3>值得信赖</h3>
            <p>专业技术团队，大品牌有保障；数据加密存储，系统稳定性高达 99.9%。</p>
          </div>
        </div>

        <!-- Card 5 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 4 }" @click="activeWhyCard = 4">
          <div class="why-icon"><el-icon><Service /></el-icon></div>
          <div class="why-content">
            <h3>服务极致</h3>
            <p>7×24 小时真人在线，疑问随时有人答；一对一专属指导，手把手带教，助您快速上手。</p>
          </div>
        </div>

        <!-- Card 6 -->
        <div class="why-card" :class="{ 'active-card': activeWhyCard === 5 }" @click="activeWhyCard = 5">
          <div class="why-icon"><el-icon><Connection /></el-icon></div>
          <div class="why-content">
            <h3>资源优质</h3>
            <p>对接主流ERP、电商平台和全球物流渠道；携手旗下优质服务商，覆盖超百万客户群。</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 4. 统计卡片区 -->
    <div class="stats-section">
      <div class="stat-card" v-for="(stat, index) in statCards" :key="index" :class="'stat-' + stat.type">
        <div class="stat-icon">
          <el-icon :size="32"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ animatedStats[stat.key] || 0 }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 3. 快捷功能区 -->
    <div class="quick-actions-section" v-if="user">
      <div class="action-card" @click="$router.push('/Storage')">
        <div class="action-icon storage-icon">
          <el-icon :size="32"><OfficeBuilding /></el-icon>
        </div>
        <div class="action-text">
          <div class="action-title">仓库管理</div>
          <div class="action-desc">管理所有仓库信息</div>
        </div>
      </div>
      <div class="action-card" @click="$router.push('/Goods')">
        <div class="action-icon goods-icon">
          <el-icon :size="32"><ShoppingBag /></el-icon>
        </div>
        <div class="action-text">
          <div class="action-title">商品管理</div>
          <div class="action-desc">查看库存与出入库</div>
        </div>
      </div>
      <div class="action-card" v-if="user?.roleId === 2" @click="$router.push('/Cart')">
        <div class="action-icon cart-icon">
          <el-icon :size="32"><ShoppingCart /></el-icon>
        </div>
        <div class="action-text">
          <div class="action-title">购物车</div>
          <div class="action-desc">待结算商品</div>
        </div>
      </div>
      <div class="action-card" @click="$router.push('/Order')">
        <div class="action-icon order-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="action-text">
          <div class="action-title">订单管理</div>
          <div class="action-desc">历史订单记录</div>
        </div>
      </div>
    </div>

    <!-- 4. 内容区域 -->
    <div class="content-section">
      <!-- 左侧：仓库列表 -->
      <div class="content-col">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><OfficeBuilding /></el-icon> 仓库一览</span>
              <el-tag type="primary" size="small">共 {{ storages.length }} 个</el-tag>
            </div>
          </template>
          <div class="storage-scroll-container">
            <div class="storage-grid">
              <div 
                class="storage-item" 
                v-for="storage in storages" 
                :key="storage.id"
                @click="openStorageModal(storage)"
              >
                <div class="storage-icon">
                  <el-icon :size="24"><Box /></el-icon>
                </div>
                <div class="storage-info">
                  <div class="storage-name">{{ storage.name }}</div>
                  <div class="storage-remark">{{ storage.remark || storage.address || '暂无描述' }}</div>
                </div>
              </div>
              <el-empty v-if="storages.length === 0" description="暂无仓库数据" :image-size="60" />
            </div>
          </div>
          <div class="card-footer" v-if="storages.length > 6">
            <el-button type="primary" link @click="$router.push('/Storage')">
              查看全部 {{ storages.length }} 个仓库
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 右侧：热门商品 -->
      <div class="content-col">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><TrendCharts /></el-icon> 库存 TOP 5</span>
              <el-tag type="success" size="small">热门商品</el-tag>
            </div>
          </template>
          <div class="goods-list">
            <div 
              class="goods-item" 
              v-for="(goods, index) in topGoods" 
              :key="goods.id"
              @click="openGoodsModal(goods)"
            >
              <div class="goods-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="goods-info">
                <div class="goods-name">{{ goods.name }}</div>
                <div class="goods-meta">
                  <span class="goods-price">￥{{ goods.price }}</span>
                  <span class="goods-unit">/ {{ goods.unit || '件' }}</span>
                </div>
              </div>
              <div class="goods-stock">
                <div class="stock-value">{{ goods.count }}</div>
                <div class="stock-label">库存</div>
              </div>
            </div>
            <el-empty v-if="topGoods.length === 0" description="暂无商品数据" :image-size="60" />
          </div>
        </el-card>
      </div>
    </div>

    <!-- 仓库详情模态框 -->
    <Teleport to="body">
      <transition name="modal-fade">
        <div class="storage-modal-overlay" v-if="selectedStorage" @click="closeStorageModal">
          <div class="storage-modal" @click.stop>
            <span class="modal-close-btn" @click="closeStorageModal">
              <el-icon :size="24"><Close /></el-icon>
            </span>
            <div class="modal-content">
              <div class="modal-header">
                <div class="modal-icon">
                  <el-icon :size="40"><OfficeBuilding /></el-icon>
                </div>
                <div class="modal-title-section">
                  <h2 class="modal-title">{{ selectedStorage.name }}</h2>
                  <el-tag :type="getTypeColor(selectedStorage.type)" effect="dark">
                    {{ selectedStorage.type || '普通仓库' }}
                  </el-tag>
                </div>
              </div>
              <div class="modal-info-grid">
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><Location /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">仓库地址</span>
                    <span class="info-card-value">{{ selectedStorage.address || '暂无地址' }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><Phone /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">联系电话</span>
                    <span class="info-card-value">{{ selectedStorage.phone || '暂无电话' }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><User /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">负责人</span>
                    <span class="info-card-value">{{ selectedStorage.manager || '暂无负责人' }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><Box /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">仓库面积</span>
                    <span class="info-card-value">{{ selectedStorage.area ? selectedStorage.area + ' m²' : '暂无面积' }}</span>
                  </div>
                </div>
              </div>
              <div class="modal-description">
                <h3 class="desc-title"><el-icon><Document /></el-icon> 仓库介绍</h3>
                <p class="desc-content">{{ selectedStorage.remark || '暂无介绍信息' }}</p>
              </div>
              <div class="modal-actions">
                <el-button type="primary" @click="$router.push('/Storage')">查看更多</el-button>
                <el-button @click="closeStorageModal">关闭</el-button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 商品详情模态框 -->
    <Teleport to="body">
      <transition name="modal-fade">
        <div class="storage-modal-overlay" v-if="selectedGoods" @click="closeGoodsModal">
          <div class="storage-modal goods-modal" @click.stop>
            <span class="modal-close-btn" @click="closeGoodsModal">
              <el-icon :size="24"><Close /></el-icon>
            </span>
            <div class="modal-content">
              <div class="modal-header">
                <div class="modal-icon goods-modal-icon">
                  <el-icon :size="40"><ShoppingBag /></el-icon>
                </div>
                <div class="modal-title-section">
                  <h2 class="modal-title">{{ selectedGoods.name }}</h2>
                  <div class="goods-tags">
                    <el-tag type="danger" effect="dark">热销商品</el-tag>
                    <el-tag type="warning" effect="plain">库存 {{ selectedGoods.count }} {{ selectedGoods.unit || '件' }}</el-tag>
                  </div>
                </div>
              </div>
              <div class="goods-price-section">
                <div class="price-label">商品价格</div>
                <div class="price-value">
                  <span class="currency">￥</span>
                  <span class="amount">{{ selectedGoods.price }}</span>
                  <span class="unit">/ {{ selectedGoods.unit || '件' }}</span>
                </div>
              </div>
              <div class="modal-info-grid">
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><Box /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">库存数量</span>
                    <span class="info-card-value">{{ selectedGoods.count }} {{ selectedGoods.unit || '件' }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><OfficeBuilding /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">所属仓库</span>
                    <span class="info-card-value">{{ getStorageName(selectedGoods.storageId) }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><PriceTag /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">商品分类</span>
                    <span class="info-card-value">{{ selectedGoods.goodstypeName || '未分类' }}</span>
                  </div>
                </div>
                <div class="modal-info-card">
                  <el-icon class="info-card-icon"><Coin /></el-icon>
                  <div class="info-card-content">
                    <span class="info-card-label">库存价值</span>
                    <span class="info-card-value">￥{{ (selectedGoods.price * selectedGoods.count).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
              <div class="modal-description">
                <h3 class="desc-title"><el-icon><Document /></el-icon> 商品描述</h3>
                <p class="desc-content">{{ selectedGoods.remark || '暂无商品描述信息' }}</p>
              </div>
              <div class="modal-actions">
                <el-button type="primary" @click="$router.push('/Goods')">查看更多商品</el-button>
                <el-button @click="closeGoodsModal">关闭</el-button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
</div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import bannerImg from '@/assets/banner.png';
import { 
  Clock, OfficeBuilding, Box, TrendCharts, Grid, Goods, ShoppingCart, Document, User,
  DataBoard, ShoppingBag, Coin, List, Close, Location, Phone, PriceTag, Search, ArrowRight,
  Lock, Cpu, Money, Timer, Medal, Service, Connection, Place
} from '@element-plus/icons-vue';

const router = useRouter();
const user = JSON.parse(sessionStorage.getItem('user'));
const currentTime = ref('');
const searchKeyword = ref('');
const activeWhyCard = ref(null); // 记录当前选中的卡片
let timer = null;

// 统计数据
const stats = ref({ storageCount: 0, goodsCount: 0, totalStock: 0, todayOrders: 0 });
const animatedStats = reactive({ storageCount: 0, goodsCount: 0, totalStock: 0, todayOrders: 0 });

const statCards = [
  { key: 'storageCount', label: '仓库总数', icon: 'OfficeBuilding', type: 'purple' },
  { key: 'goodsCount', label: '物品种类', icon: 'ShoppingBag', type: 'blue' },
  { key: 'totalStock', label: '库存总量', icon: 'Coin', type: 'green' },
  { key: 'todayOrders', label: '今日订单', icon: 'List', type: 'orange' }
];

const storages = ref([]);
const topGoods = ref([]);
const selectedStorage = ref(null);
const selectedGoods = ref(null);

// 搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  sessionStorage.setItem('globalSearch', searchKeyword.value.trim());
  router.push('/Goods');
};

const openStorageModal = (storage) => { selectedStorage.value = storage; };
const closeStorageModal = () => { selectedStorage.value = null; };
const openGoodsModal = (goods) => { selectedGoods.value = goods; };
const closeGoodsModal = () => { selectedGoods.value = null; };

const getTypeColor = (type) => {
  const colorMap = { '电商仓库': 'primary', '冷链仓库': 'info', '保税仓库': 'warning', '普通仓库': 'success' };
  return colorMap[type] || 'info';
};

const getStorageName = (storageId) => {
  const storage = storages.value.find(s => s.id === storageId);
  return storage ? storage.name : '未知仓库';
};

const getGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  return '晚上好';
};

const getWelcomeMessage = () => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了，注意休息~';
  if (hour < 9) return '新的一天，元气满满！';
  if (hour < 12) return '上午工作顺利！';
  if (hour < 14) return '午餐时间，记得休息~';
  if (hour < 17) return '下午效率加倍！';
  if (hour < 19) return '忙碌了一天，辛苦了！';
  if (hour < 22) return '晚间时光，放松心情~';
  return '夜已深，早点休息吧！';
};

const animateNumber = (key, target) => {
  const duration = 1000;
  const steps = 60;
  const stepValue = target / steps;
  let current = 0;
  const interval = setInterval(() => {
    current += stepValue;
    if (current >= target) {
      animatedStats[key] = target;
      clearInterval(interval);
    } else {
      animatedStats[key] = Math.floor(current);
    }
  }, duration / steps);
};

const loadDashboardData = async () => {
  try {
    const res = await axios.get('http://localhost:8090/dashboard/stats');
    if (res.data.code === 200) {
      stats.value = res.data.data;
      Object.keys(stats.value).forEach(key => animateNumber(key, stats.value[key]));
    }
  } catch (e) { console.error('加载统计数据失败', e); }
};

const loadStorages = async () => {
  try {
    const res = await axios.get('http://localhost:8090/storage/list');
    if (res.data.code === 200) storages.value = res.data.data || [];
  } catch (e) { console.error('加载仓库失败', e); }
};

const loadTopGoods = async () => {
  try {
    const res = await axios.post('http://localhost:8090/goods/listPage', { pageNum: 1, pageSize: 5, param: {} });
    if (res.data.code === 200) {
      topGoods.value = (res.data.data || []).sort((a, b) => b.count - a.count).slice(0, 5);
    }
  } catch (e) { console.error('加载商品失败', e); }
};

onMounted(() => {
  loadDashboardData();
  loadStorages();
  loadTopGoods();
});

onBeforeUnmount(() => { if (timer) clearInterval(timer); });
</script>

<style scoped>
.home-page {
  min-height: 100%;
}

/* Carousel Wrapper */
.carousel-wrapper {
  position: relative;
  width: 100%;
  margin-bottom: 24px;
}

.carousel-slide {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
}

.slide-1 { background: linear-gradient(135deg, #1e1e2f 0%, #2d2d44 100%); }
.slide-2 { background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%); }
.slide-3 { background: linear-gradient(135deg, #0f766e 0%, #115e59 100%); }

.slide-overlay {
  position: absolute; inset: 0;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(255,255,255,0.05), transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(255,255,255,0.05), transparent 40%);
}

.slide-content {
  position: relative; z-index: 2; text-align: center; color: #fff;
  padding: 0 20px;
  animation: slideUp 0.8s ease-out;
}
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.slide-title {
  font-size: 42px; font-weight: 700; margin: 0 0 16px 0; letter-spacing: 2px;
  text-shadow: 0 4px 12px rgba(0,0,0,0.2);
}
.slide-subtitle {
  font-size: 18px; color: rgba(255,255,255,0.9); max-width: 600px; margin: 0 auto; line-height: 1.6;
}

/* Floating Search Bar */
.hero-float-content {
  position: absolute;
  bottom: 60px; left: 0; right: 0;
  z-index: 10;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  pointer-events: none; /* Allow clicks to pass through empty areas */
}
.hero-float-content > * { pointer-events: auto; } /* Re-enable clicks on children */

/* Intro Section */
.intro-section {
  max-width: 1200px; margin: 0 auto 60px; padding: 0 24px;
  text-align: center;
}
.intro-header { margin-bottom: 40px; }
.intro-title { font-size: 32px; font-weight: 700; color: #1e293b; margin: 0 0 12px; }
.intro-divider { width: 60px; height: 4px; background: linear-gradient(to right, #667eea, #764ba2); margin: 0 auto 20px; border-radius: 2px; }
.intro-desc { font-size: 16px; color: #64748b; line-height: 1.8; max-width: 700px; margin: 0 auto; }

.intro-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px;
}
.intro-card {
  background: #fff; border-radius: 20px; padding: 32px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
}
.intro-card:hover { transform: translateY(-10px); box-shadow: 0 20px 50px rgba(0,0,0,0.1); border-color: #e2e8f0; }
.intro-icon {
  width: 64px; height: 64px; margin: 0 auto 20px;
  background: #f8fafc; border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  font-size: 32px; color: #667eea;
  transition: all 0.3s;
}
.intro-card:hover .intro-icon { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; }
.intro-card-title { font-size: 20px; font-weight: 600; color: #1e293b; margin: 0 0 12px; }
.intro-card-text { font-size: 14px; color: #64748b; line-height: 1.6; }

/* Hero Search */
.hero-search {
  display: flex;
  align-items: center;
  max-width: 700px; /* Reduced width */
  margin: 0 auto 24px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50px;
  padding: 4px 4px 4px 20px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
}

.hero-search .search-icon {
  color: #9ca3af;
  font-size: 20px;
  margin-right: 12px;
}

.hero-search .search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: #1f2937;
  background: transparent;
}

.hero-search .search-input::placeholder {
  color: #9ca3af;
}

.hero-search .search-btn {
  height: 40px; 
  padding: 0 24px;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* Quick Tags */
.quick-tags {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-tag {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-tag:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

/* Common Centered Container Styles */
.stats-section,
.quick-actions-section,
.content-section {
  max-width: 1600px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 24px;
  padding-right: 24px;
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-purple { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-blue { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); }
.stat-green { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
.stat-orange { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }

.stat-icon {
  width: 60px; height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}

.stat-info { flex: 1; }
.stat-value { font-size: 32px; font-weight: 700; color: #fff; line-height: 1.2; }
.stat-label { font-size: 14px; color: rgba(255, 255, 255, 0.85); margin-top: 4px; }

/* Quick Actions */
.quick-actions-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.action-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.action-icon {
  width: 56px; height: 56px;
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}

.storage-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.goods-icon { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); }
.cart-icon { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
.order-icon { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }

.action-title { font-size: 16px; font-weight: 600; color: #1f2937; }
.action-desc { font-size: 13px; color: #9ca3af; margin-top: 4px; }

/* Content Section */
.content-section { 
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  align-items: stretch;
}

/* Why Choose Us Section */
.why-section {
  max-width: 1400px;
  margin: 0 auto 60px;
  padding: 0 24px;
}

.why-header {
  text-align: center;
  margin-bottom: 50px;
  animation: fadeInDown 0.8s ease-out;
}
.why-title {
  font-size: 36px; font-weight: 800; color: #1e293b; margin: 0 0 16px;
}
.why-subtitle {
  font-size: 18px; color: #64748b; font-weight: 500;
}

.why-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.why-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 32px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 1px solid #f1f5f9;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
  animation: fadeInUp 0.8s ease-out backwards;
}

.why-card:nth-child(1) { animation-delay: 0.1s; }
.why-card:nth-child(2) { animation-delay: 0.2s; }
.why-card:nth-child(3) { animation-delay: 0.3s; }
.why-card:nth-child(4) { animation-delay: 0.4s; }
.why-card:nth-child(5) { animation-delay: 0.5s; }
.why-card:nth-child(6) { animation-delay: 0.6s; }

.why-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0,0,0,0.08);
}

.why-icon {
  width: 64px; height: 64px;
  font-size: 32px; color: #0066ff;
  display: flex; align-items: center; justify-content: flex-start;
}

.why-content h3 {
  font-size: 22px; font-weight: 700; color: #1e293b; margin: 0 0 12px;
}
.why-content p {
  font-size: 15px; color: #64748b; line-height: 1.6; margin: 0;
}

/* Active Blue Card Style */
.why-card.active-card {
  background: linear-gradient(135deg, #0066ff 0%, #0052cc 100%);
  border: none;
  color: #fff;
  transform: scale(1.05);
  box-shadow: 0 20px 50px rgba(0, 102, 255, 0.3);
  justify-content: center;
}
.why-card.active-card:hover {
  transform: scale(1.05) translateY(-5px);
}
.why-card.active-card h3 { color: #fff; font-size: 24px; }
.why-card.active-card p { color: rgba(255,255,255,0.9); font-size: 16px; }

@keyframes fadeInDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

.content-col {
  flex: 1;
  min-width: 0;
}

.content-card {
  height: 100%;
}

.content-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.card-header .el-icon { margin-right: 8px; color: #667eea; }

.card-footer { text-align: center; padding-top: 12px; border-top: 1px solid #f3f4f6; margin-top: 12px; }

/* Storage Scroll Container */
.storage-scroll-container {
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
}

.storage-scroll-container::-webkit-scrollbar {
  width: 6px;
}

.storage-scroll-container::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.storage-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* Storage Grid */
.storage-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.storage-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.storage-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.storage-icon {
  width: 44px; height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
}

.storage-name { font-size: 14px; font-weight: 600; color: #1f2937; }
.storage-remark { font-size: 12px; color: #9ca3af; margin-top: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 120px; }

/* Goods List */
.goods-list { display: flex; flex-direction: column; gap: 12px; }

.goods-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.goods-item:hover { background: #f8fafc; transform: translateX(4px); }

.goods-rank {
  width: 28px; height: 28px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; color: #fff;
}

.rank-1 { background: linear-gradient(135deg, #f59e0b, #d97706); }
.rank-2 { background: linear-gradient(135deg, #9ca3af, #6b7280); }
.rank-3 { background: linear-gradient(135deg, #d97706, #b45309); }
.rank-4, .rank-5 { background: #e5e7eb; color: #6b7280; }

.goods-info { flex: 1; }
.goods-name { font-size: 14px; font-weight: 600; color: #1f2937; }
.goods-meta { font-size: 12px; margin-top: 2px; }
.goods-price { color: #ef4444; font-weight: 600; }
.goods-unit { color: #9ca3af; }

.goods-stock { text-align: right; }
.stock-value { font-size: 20px; font-weight: 700; color: #667eea; }
.stock-label { font-size: 11px; color: #9ca3af; }

/* Modal Styles */
.storage-modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999;
  padding: 20px;
}

.storage-modal {
  background: #fff;
  border-radius: 24px;
  width: 100%; max-width: 560px; max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  animation: modalZoomIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalZoomIn {
  from { opacity: 0; transform: scale(0.85) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.modal-close-btn {
  position: absolute;
  top: 16px; right: 16px;
  width: 40px; height: 40px;
  border-radius: 50%;
  background: #f3f4f6;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: #6b7280;
  transition: all 0.3s ease;
  z-index: 10;
}

.modal-close-btn:hover { background: #e5e7eb; color: #1f2937; transform: rotate(90deg); }

.modal-content { padding: 32px; }

.modal-header { display: flex; align-items: center; gap: 20px; margin-bottom: 28px; }

.modal-icon {
  width: 72px; height: 72px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  box-shadow: 0 10px 28px rgba(102, 126, 234, 0.35);
}

.goods-modal-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%) !important; box-shadow: 0 10px 28px rgba(240, 147, 251, 0.35) !important; }

.modal-title-section { flex: 1; }
.modal-title { font-size: 24px; font-weight: 700; color: #1f2937; margin: 0 0 8px 0; }
.goods-tags { display: flex; gap: 8px; }

.modal-info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; margin-bottom: 20px; }

.modal-info-card { display: flex; align-items: flex-start; gap: 12px; padding: 14px; background: #f8fafc; border-radius: 12px; transition: all 0.3s ease; }
.modal-info-card:hover { background: #f1f5f9; transform: translateX(4px); }
.info-card-icon { font-size: 22px; color: #667eea; flex-shrink: 0; }
.info-card-content { display: flex; flex-direction: column; gap: 4px; }
.info-card-label { font-size: 12px; color: #9ca3af; }
.info-card-value { font-size: 14px; color: #1f2937; font-weight: 500; word-break: break-all; }

.modal-description { background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); border-radius: 14px; padding: 18px; margin-bottom: 20px; }
.desc-title { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #667eea; margin: 0 0 10px 0; }
.desc-content { font-size: 14px; color: #4b5563; line-height: 1.7; margin: 0; }

.modal-actions { display: flex; justify-content: flex-end; gap: 12px; padding-top: 16px; border-top: 1px solid #f3f4f6; }

.goods-price-section { background: linear-gradient(135deg, #fff5f5 0%, #fff0f3 100%); border-radius: 14px; padding: 18px; margin-bottom: 20px; text-align: center; }
.price-label { font-size: 13px; color: #9ca3af; margin-bottom: 6px; }
.price-value { display: flex; align-items: baseline; justify-content: center; gap: 2px; }
.price-value .currency { font-size: 20px; color: #f5576c; font-weight: 600; }
.price-value .amount { font-size: 38px; color: #f5576c; font-weight: 700; line-height: 1; }
.price-value .unit { font-size: 14px; color: #9ca3af; margin-left: 4px; }

.modal-fade-enter-active { animation: fadeIn 0.3s ease-out; }
.modal-fade-leave-active { animation: fadeIn 0.2s ease-in reverse; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>