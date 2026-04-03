/**
 * auth.model.ts — Mapping ResLoginDTO & ReqLoginDTO từ backend.
 *
 * Backend: com.quyen.shoplite.domain.response.ResLoginDTO
 */

/** Thông tin user rút gọn trả về sau login */
export interface UserInfo {
  id: number;
  username: string;
  roleName: string;
}

/** Shape của data trong response login — mapping ResLoginDTO */
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: UserInfo;
}

/** Wrapper chung cho mọi response từ backend */
export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  data: T;
}

/** Payload gửi lên khi đăng nhập — mapping ReqLoginDTO */
export interface LoginRequest {
  username: string;
  password: string;
}
