import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 1. 新增登录页路由
        {
            path: '/login',
            name: 'Login',
            component: () => import('../components/Login.vue')
        },
        // 2. 主框架路由
        {
            path: '/',
            name: 'Index',
            component: () => import('../components/Index.vue'),
            redirect: '/login', // <--- 修改这里：默认先去登录页
            children: [
                {
                    path: '/Home',
                    name: 'Home',
                    component: () => import('../components/Home.vue')
                },
                {
                    path: '/User',
                    name: 'User',
                    component: () => import('../components/UserManage.vue')
                },
                {
                    path: '/Storage',
                    name: 'Storage',
                    component: () => import('../components/StorageManage.vue')
                },
                {
                    path: '/Goods',
                    name: 'Goods',
                    component: () => import('../components/GoodsManage.vue')
                },
                {
                    path: '/Cart',
                    name: 'Cart',
                    component: () => import('../components/CartManage.vue')
                },
                {
                    path: '/Order',
                    name: 'Order',
                    component: () => import('../components/OrderManage.vue')
                },
                {
                    path: '/Person',
                    name: 'Person',
                    component: () => import('../components/Person.vue')
                },
            ]
        }
    ]
})

export default router