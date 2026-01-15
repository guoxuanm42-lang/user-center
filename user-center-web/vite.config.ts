import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 函数级注释：创建 Vite 配置，设置别名与开发代理。
// 小白理解：告诉 Vite “@ 就是 src”，并把 /user、/db 的请求转接到后端 8080。
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/user': { target: 'http://localhost:8090', changeOrigin: true },
      '/admin': { target: 'http://localhost:8090', changeOrigin: true },
    },
  },
})
