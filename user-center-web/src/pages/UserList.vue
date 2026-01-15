<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { searchUsers } from '@/api/http'
import type { UserDTO } from '@/api/dto'

const keyword = ref('')
const users = ref<UserDTO[]>([])

/* 函数级注释：触发搜索，请求后端并刷新表格。
   小白理解：点“搜索”或回车，把词交给后端，拿回列表。 */
async function doSearch() {
  const data = await searchUsers(keyword.value)
  users.value = data
}

onMounted(doSearch)
</script>

<template>
  <div class="panel">
    <div class="panel-head">
      <strong>用户列表</strong>
      <div style="color: var(--text-secondary);">共 {{ users.length }} 条</div>
    </div>
    <div class="panel-body">
      <div class="form-row" style="margin-bottom: 10px;">
        <div>
          <div class="label">关键词</div>
          <input v-model="keyword" class="input" placeholder="按用户名模糊搜索" @keyup.enter="doSearch" />
        </div>
        <button class="btn btn-primary" @click="doSearch">搜索</button>
        <button class="btn btn-secondary" @click="keyword=''; doSearch()">重置</button>
      </div>

      <table>
        <thead>
          <tr><th>ID</th><th>用户名</th></tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.name }}</td>
          </tr>
          <tr v-if="users.length === 0"><td colspan="2">暂无数据</td></tr>
        </tbody>
      </table>

      <div class="pagination">
        <button class="btn btn-secondary">上一页</button>
        <button class="btn btn-secondary">下一页</button>
      </div>
    </div>
  </div>
</template>
