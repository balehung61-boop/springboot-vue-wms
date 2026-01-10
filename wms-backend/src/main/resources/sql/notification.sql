-- 通知功能数据库表
-- 请在 MySQL 中执行此文件来创建通知表

CREATE TABLE IF NOT EXISTS sys_notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '接收用户ID (0表示系统广播)',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(20) DEFAULT 'system' COMMENT '通知类型: system/order/stock',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- 插入测试数据
INSERT INTO sys_notification (user_id, title, content, type, is_read) VALUES
(0, '🎉 系统更新通知', '新版本已上线！本次更新包含通知功能、性能优化等多项改进。', 'system', 0),
(0, '📢 新功能上线', '商品管理新增批量导入功能，快来体验吧！', 'system', 0),
(0, '🔧 系统维护通知', '系统将于周日22:00-23:00进行例行维护，届时服务可能短暂不可用。', 'system', 0),
(1, '📦 订单确认', '您的订单 #2024010001 已确认，正在备货中。', 'order', 0),
(1, '⚠️ 库存预警', '商品【无线蓝牙耳机】库存不足10件，请及时补货！', 'stock', 0),
(2, '🛒 订单发货通知', '您的订单 #2024010002 已发货，请注意查收。', 'order', 0);
