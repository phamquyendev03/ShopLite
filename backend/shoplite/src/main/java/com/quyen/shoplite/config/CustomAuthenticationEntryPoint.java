package com.quyen.shoplite.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Trả về JSON 401 khi request không có token hoặc token không hợp lệ.
 * Dùng getWriter() – không cần ObjectMapper để tránh phụ thuộc ngoài.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = authException.getMessage() != null
                ? authException.getMessage().replace("\"", "'")
                : "Unauthorized";

        String json = """
                {
                  "statusCode": 401,
                  "message": "Không có quyền truy cập: %s",
                  "data": null
                }
                """.formatted(message);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
