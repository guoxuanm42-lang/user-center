<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-sider">
      <div class="admin-sider-head">后台管理模块</div>
      <nav class="admin-menu">
        <RouterLink to="/admin/portal">后台首页</RouterLink>
        <RouterLink to="/admin/users">用户管理</RouterLink>
      </nav>
      <div class="admin-sider-footer">
        <RouterLink class="back-link" to="/profile">返回主界面</RouterLink>
      </div>
    </aside>
    <section class="admin-main">
      <RouterView />
    </section>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-soft);
}
.admin-sider {
  width: 200px;
  padding: 12px;
  border-right: 1px solid var(--panel-border);
  background: #fff;
}
.admin-sider-head {
  font-weight: 700;
  color: var(--brand-primary);
  margin-bottom: 10px;
}
.admin-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.admin-menu a {
  color: var(--text-primary);
  text-decoration: none;
  padding: 8px 10px;
  border-radius: 6px;
}
.admin-menu a.router-link-active {
  background: rgba(53, 205, 195, 0.12);
  color: var(--brand-primary);
  font-weight: 600;
}
.admin-main {
  flex: 1;
  padding: 16px;
}
.admin-sider-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--panel-border);
}
.back-link {
  display: block;
  text-decoration: none;
  padding: 8px 10px;
  border-radius: 6px;
  background: #fff;
  color: var(--brand-primary);
  border: 1px solid var(--brand-primary);
  text-align: center;
}
.back-link:hover {
  background: rgba(53, 205, 195, 0.08);
}
</style>
