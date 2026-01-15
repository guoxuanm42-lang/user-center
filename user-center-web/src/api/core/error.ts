import type { ApiEnvelope } from './types'

/* 函数级注释：判断后端返回值是不是统一结构（code/message/data）。
   小白理解：后端现在会把数据包一层“盒子”，这里负责识别这个盒子。 */
export function isApiEnvelope(payload: unknown): payload is ApiEnvelope {
  if (!payload || typeof payload !== 'object') return false
  return 'code' in payload && 'message' in payload && 'data' in payload
}

/* 函数级注释：从 Error 对象里尝试拿到后端错误码。
   小白理解：我们把 code 挂在 Error 上，这里就是把它安全地取出来。 */
export function getErrorCode(e: unknown): number | null {
  if (!e || typeof e !== 'object') return null
  if (!('code' in e)) return null
  const code = (e as { code?: unknown }).code
  return typeof code === 'number' ? code : null
}

