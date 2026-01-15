<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
const user = useUserStore()
const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

onMounted(() => {
  user.initLoginState().catch(() => {})
})
</script>

<template>
  <header v-if="!isAdminRoute" class="app-header">
    <RouterLink class="brand-link" to="/profile">用户中心</RouterLink>
    <RouterLink to="/login">登录</RouterLink>
    <RouterLink v-if="user.currentUser?.userRole === 1" to="/admin">后台管理模块</RouterLink>
    <div class="header-right">
      <span v-if="user.currentUser">你好，{{ user.currentUser.name }}</span>
      <span v-else>未登录</span>
    </div>
  </header>
  <main :style="isAdminRoute ? 'padding:0;' : 'padding:16px;'">
    <RouterView />
  </main>
</template>
