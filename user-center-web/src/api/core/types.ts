export type ApiResponse<T> = {
  code: number
  message: string
  data: T
  timestamp: number
}

export type ApiEnvelope = {
  code: unknown
  message: unknown
  data: unknown
  timestamp?: unknown
}

export type ApiError = Error & { code?: number }

