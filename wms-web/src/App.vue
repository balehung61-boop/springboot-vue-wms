<template>
  <div id="app">
    <router-view></router-view>
    <!-- 鼠标拖尾粒子容器 -->
    <div class="cursor-trail-container" ref="trailContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const trailContainer = ref(null);
let particles = [];
let animationId = null;

// 粒子类
class Particle {
  constructor(x, y, container) {
    this.x = x;
    this.y = y;
    this.size = Math.random() * 4 + 2; // [Modified] 更细小: 2px - 6px
    this.speedX = (Math.random() - 0.5) * 8; // [Modified] 速度更快，范围更大
    this.speedY = (Math.random() - 0.5) * 8;
    this.life = 1;
    this.decay = Math.random() * 0.015 + 0.01; // [Modified] 存活更久

    // 随机颜色
    const hue = Math.floor(Math.random() * 360);
    const color = `hsl(${hue}, 80%, 60%)`;

    // 创建DOM元素
    this.element = document.createElement('div');
    this.element.className = 'cursor-particle';
    this.element.style.cssText = `
      position: fixed;
      pointer-events: none;
      width: ${this.size}px;
      height: ${this.size}px;
      left: ${this.x}px;
      top: ${this.y}px;
      background: ${color}; 
      box-shadow: 0 0 4px ${color};
      border-radius: 50%;
      z-index: 9999;
      opacity: ${this.life};
      transform: translate(-50%, -50%);
      transition: transform 0.1s;
    `;
    container.appendChild(this.element);
  }

  update() {
    this.x += this.speedX;
    this.y += this.speedY;
    this.life -= this.decay;
    
    // 简单的物理模拟：速度慢慢变慢
    this.speedX *= 0.96;
    this.speedY *= 0.96;
    
    this.size *= 0.96; // 慢慢变小

    if (this.life > 0) {
        this.element.style.left = `${this.x}px`;
        this.element.style.top = `${this.y}px`;
        this.element.style.opacity = this.life;
        this.element.style.width = `${this.size}px`;
        this.element.style.height = `${this.size}px`;
    }

    return this.life > 0;
  }

  remove() {
    if (this.element.parentNode) {
      this.element.parentNode.removeChild(this.element);
    }
  }
}

// 鼠标移动处理
const handleMouseMove = (e) => {
  if (!trailContainer.value) return;
  
  // 每次移动创建粒子，范围扩大
  for (let i = 0; i < 4; i++) {
    particles.push(new Particle(
      e.clientX + (Math.random() - 0.5) * 20, // [Modified] 初始生成范围更大
      e.clientY + (Math.random() - 0.5) * 20,
      trailContainer.value
    ));
  }
};

// 动画循环
const animate = () => {
  particles = particles.filter(particle => {
    const alive = particle.update();
    if (!alive) {
      particle.remove();
    }
    return alive;
  });
  
  animationId = requestAnimationFrame(animate);
};

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove);
  animate();
});

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove);
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  particles.forEach(p => p.remove());
  particles = [];
});
</script>

<style>
#app {
  height: 100%;
}

.cursor-trail-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  overflow: hidden;
}
</style>