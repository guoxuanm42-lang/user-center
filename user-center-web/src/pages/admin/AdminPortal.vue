<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const user = useUserStore()
const router = useRouter()

/* 函数级注释：后台管理入口页（管理员专用）。
   小白理解：这是管理员进入“后台管理模块”的首页，放一些常用功能入口按钮。 */
onMounted(() => {
  user.initLoginState().catch(() => {})
})

const adminName = computed(() => user.currentUser?.name || '管理员')

/* 函数级注释：进入用户管理页面。
   小白理解：点一下按钮，就跳到“用户管理”列表页。 */
function goUserManage() {
  router.push('/admin/users')
}
</script>

<template>
  <div class="panel" style="max-width: 720px; margin: 16px auto;">
    <div class="panel-head"><strong>后台管理模块</strong></div>
    <div class="panel-body">
      <p>欢迎，{{ adminName }}</p>
      <div class="form-row" style="margin-top: 8px;">
        <button class="btn btn-primary" @click="goUserManage">进入用户管理</button>
      </div>
    </div>
  </div>
</template>