<template>
  <div>
    <!-- 1. 顶部搜索栏 -->
    <div style="margin-bottom: 20px;">
      <el-input v-model="name" placeholder="请输入物品名" style="width: 200px;" @keyup.enter="loadPost"></el-input>

      <!-- 搜索时也可以加个仓库筛选，这里先做简单的 -->
      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="reset">重置</el-button>
      <!-- 加上 v-if="user.roleId != 2" -->
      <el-button type="primary" @click="add" v-if="user.roleId != 2">新增</el-button>
    </div>

    <!-- 2. 中间表格 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="物品名" width="150" />

      <!-- 把仓库ID转换成仓库名显示 -->
      <el-table-column prop="storageId" label="所属仓库" width="150" :formatter="formatStorage">
      </el-table-column>

      <!-- 把分类ID转换成分类名显示 -->
      <el-table-column prop="goodstypeId" label="分类" width="150" :formatter="formatGoodsType">
      </el-table-column>

      <el-table-column prop="price" label="单价" width="100">
        <template #default="scope">
          <span style="color: red;">￥{{ scope.row.price }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="count" label="库存数量" width="100">
        <template #default="scope">
          <el-tag effect="dark" :type="scope.row.count < 10 ? 'danger' : 'primary'">{{ scope.row.count }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="remark" label="备注" />


      <el-table-column label="操作" width="200">
        <template #default="scope">

          <!-- 场景1：管理员 (roleId != 2) 显示管理按钮 -->
          <div v-if="user.roleId != 2">
            <el-button size="small" type="primary" plain @click="inGoods(scope.row)">入库</el-button>
            <el-button size="small" type="success" plain @click="outGoods(scope.row)">出库</el-button>
            <el-button size="small" type="primary" :icon="Edit" circle @click="mod(scope.row)"></el-button>
            <el-popconfirm title="确定删除吗？" @confirm="del(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger" :icon="Delete" circle></el-button>
              </template>
            </el-popconfirm>
          </div>

          <!-- 场景2：普通用户 (roleId == 2) 显示加入购物车 -->
          <div v-else>
            <el-button size="small" type="warning" @click="addCart(scope.row)">加入购物车</el-button>
          </div>

        </template>
      </el-table-column>
    </el-table>

    <!-- 3. 底部分页 -->
    <div style="padding: 10px 0;">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 4. 弹窗 (带下拉框) -->
    <el-dialog
        v-model="centerDialogVisible"
        title="物品信息"
        width="30%"
        center>

      <el-form :model="form" label-width="80px">
        <el-form-item label="物品名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="所属仓库">
          <!-- 下拉框：数据来源 storageData -->
          <el-select v-model="form.storageId" placeholder="请选择仓库" style="width: 100%;">
            <el-option
                v-for="item in storageData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="物品分类">
          <!-- 下拉框：数据来源 goodsTypeData -->
          <el-select v-model="form.goodstypeId" placeholder="请选择分类" style="width: 100%;">
            <el-option
                v-for="item in goodsTypeData"
                :key="item.id"
                :label="item.name"
                :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="物品单价">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" label="单价"></el-input-number>
        </el-form-item>

        <el-form-item label="初始库存">
          <el-input-number v-model="form.count" :min="0" label="库存"></el-input-number>
        </el-form-item>

        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出入库弹窗 -->
    <el-dialog
        v-model="inOutVisible"
        title="出入库操作"
        width="30%"
        center>

      <el-form label-width="80px">
        <el-form-item label="物品名">
          <el-input v-model="form1.goodsname" disabled></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form1.count" :min="1" label="数量"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form1.remark"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="inOutVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInOut">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- ▼▼▼ 新增：加入购物车弹窗 ▼▼▼ -->
    <el-dialog
        v-model="cartDialogVisible"
        title="加入购物车"
        width="30%"
        center>

      <el-form label-width="100px">
        <el-form-item label="物品名称">
          <el-tag>{{ cartForm.name }}</el-tag>
        </el-form-item>

        <el-form-item label="物品单价">
          <span style="color: red; font-weight: bold;">￥{{ cartForm.price }}</span>
        </el-form-item>

        <el-form-item label="购买数量">
          <!-- max 限制不能超过当前库存 -->
          <el-input-number v-model="cartForm.count" :min="1" :max="cartForm.stock" label="数量"></el-input-number>
        </el-form-item>

        <el-form-item label="总金额">
          <!-- 自动计算：单价 * 数量 -->
          <span style="color: red; font-size: 18px; font-weight: bold;">
             ￥{{ (cartForm.price * cartForm.count).toFixed(2) }}
           </span>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cartDialogVisible = false">取消</el-button>
          <el-button type="warning" @click="confirmAddCart">确定加入</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- ▲▲▲ 新增结束 ▲▲▲ -->

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { Edit, Delete } from '@element-plus/icons-vue' // 引入图标

const user = JSON.parse(sessionStorage.getItem('user'));
// 购物车弹窗控制
const cartDialogVisible = ref(false);
const cartForm = ref({
  goodsId: '',
  name: '',
  price: 0,
  count: 1,
  stock: 0 // 用来记录当前最大库存
});
// 核心数据
const tableData = ref([]);
const storageData = ref([]);   // 存所有仓库
const goodsTypeData = ref([]); // 存所有分类

const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const name = ref('');
const centerDialogVisible = ref(false);
const form = ref({
  id: '',
  name: '',
  storageId: '',
  goodstypeId: '',
  price: 0, // <--- 新增这一行，默认0元
  count: 0,
  remark: ''
})

// 1. 加载物品列表
const loadPost = () => {
  axios.post('http://localhost:8090/goods/listPage', {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    param: {
      name: name.value
    }
  }).then(res => {
    if (res.data.code === 200) {
      tableData.value = res.data.data;
      total.value = res.data.total;
    } else {
      alert('获取数据失败');
    }
  })
}

// 点击“加入购物车”按钮 -> 打开弹窗
const addCart = (row) => {
  // 1. 把当前行的数据填入表单
  cartForm.value = {
    goodsId: row.id,
    name: row.name,
    price: row.price || 0, // 防止没有单价报错
    count: 1,              // 默认买1个
    stock: row.count       // 记录当前库存用于限制最大购买数
  };

  // 2. 显示弹窗
  cartDialogVisible.value = true;
}
// 点击弹窗里的“确定加入” -> 发送请求
const confirmAddCart = () => {
  axios.post('http://localhost:8090/cart/save', {
    goodsId: cartForm.value.goodsId,
    userId: user.id,
    count: cartForm.value.count
  }).then(res => {
    if(res.data.code === 200){
      alert('加入购物车成功！');
      cartDialogVisible.value = false; // 关闭弹窗
    } else {
      alert(res.data.msg || '加入失败');
    }
  })
}
// 2. 加载所有仓库 (给下拉框用)
const loadStorage = () => {
  axios.get('http://localhost:8090/storage/list').then(res => {
    if(res.data.code === 200){
      storageData.value = res.data.data;
    }
  })
}

// 3. 加载所有分类 (给下拉框用)
const loadGoodsType = () => {
  axios.get('http://localhost:8090/goodstype/list').then(res => {
    if(res.data.code === 200){
      goodsTypeData.value = res.data.data;
    }
  })
}

// 4. 表格格式化工具：ID变名字
const formatStorage = (row) => {
  let temp = storageData.value.find(item => item.id == row.storageId);
  return temp && temp.name;
}
const formatGoodsType = (row) => {
  let temp = goodsTypeData.value.find(item => item.id == row.goodstypeId);
  return temp && temp.name;
}

const reset = () => {
  name.value = '';
  loadPost();
}

const add = () => {
  centerDialogVisible.value = true;
  nextTick(()=>{
    form.value = {id:'', name:'', storageId:'', goodstypeId:'', count:0, remark:''};
  })
}

const save = () => {
  axios.post('http://localhost:8090/goods/saveOrMod', form.value).then(res => {
    if(res.data.code === 200){
      alert('操作成功');
      centerDialogVisible.value = false;
      loadPost();
    }else{
      alert('操作失败');
    }
  })
}

const mod = (row) => {
  centerDialogVisible.value = true;
  nextTick(()=>{
    form.value = JSON.parse(JSON.stringify(row));
  })
}

const del = (id) => {
  axios.get('http://localhost:8090/goods/delete?id='+id).then(res => {
    if(res.data.code === 200){
      alert('删除成功');
      loadPost();
    }else{
      alert('删除失败');
    }
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadPost();
}
const handleCurrentChange = (val) => {
  pageNum.value = val;
  loadPost();
}

const inOutVisible = ref(false); // 控制出入库弹窗
const form1 = ref({ // 出入库表单
  goodsId: '',
  goodsname: '',
  count: '',
  remark: '',
  flag: 1 // 1:入库 2:出库
})

// 点击入库
const inGoods = (row) => {
  inOutVisible.value = true;
  nextTick(() => {
    form1.value = { goodsId: row.id, goodsname: row.name, count: 1, remark: '', flag: 1 };
  })
}

// 点击出库
const outGoods = (row) => {
  inOutVisible.value = true;
  nextTick(() => {
    form1.value = { goodsId: row.id, goodsname: row.name, count: 1, remark: '', flag: 2 };
  })
}

// 提交出入库
const saveInOut = () => {
  // 根据 flag 判断调哪个接口
  let url = form1.value.flag === 1 ? 'inStock' : 'outStock';

  axios.post(`http://localhost:8090/goods/${url}`, form1.value).then(res => {
    if(res.data.code === 200){
      alert('操作成功');
      inOutVisible.value = false;
      loadPost(); // 刷新表格看库存变没变
    }else{
      alert(res.data.msg || '操作失败');
    }
  })
}

onMounted(() => {
  loadPost();      // 查物品
  loadStorage();   // 查仓库
  loadGoodsType(); // 查分类
})
</script>

<style scoped>
</style>