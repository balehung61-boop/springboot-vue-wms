# SpringBoot Vue WMS Project

基于 Spring Boot 3 + Vue 3 的仓储管理系统 (WMS)。

## 🛠 技术栈

### 后端 (Backend)
- **Java**: 21
- **Framework**: Spring Boot 3.5.9
- **ORM**: MyBatis-Plus 3.5.7
- **Database**: MySQL
- **Tools**: Lombok, Maven

### 前端 (Frontend)
- **Framework**: Vue 3.5.26
- **Build Tool**: Vite 7.3.0
- **UI Library**: Element Plus 2.13.0
- **Routing**: Vue Router 4.6.4
- **HTTP Client**: Axios 1.13.2

## 🚀 快速开始

### 后端启动
1. 确保已安装 JDK 21 和 Maven。
2. 配置 MySQL 数据库，导入 `wms-backend/src/main/resources/sql` 下的 SQL 脚本。
3. 修改 `application.yml` 或 `application.properties` 中的数据库连接配置。
4. 进入 `wms-backend` 目录并运行 application。

### 前端启动
1. 确保已安装 Node.js (推荐 v20+)。
2. 进入 `wms-web` 目录。
3. 安装依赖：
   ```bash
   npm install
   ```
4. 启动开发服务器：
   ```bash
   npm run dev
   ```

## 📦 目录结构

```
springboot-vue-wms/
├── wms-backend/       # 后端项目源码
│   ├── src/
│   └── pom.xml
├── wms-web/           # 前端项目源码
│   ├── src/
│   └── package.json
└── README.md
```

## 📄 License

MIT
