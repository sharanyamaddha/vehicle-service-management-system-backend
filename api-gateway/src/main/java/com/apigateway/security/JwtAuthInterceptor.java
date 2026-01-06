package com.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String SECRET = "THIS_IS_VMS_SUPER_SECURE_32BYTE_LONG_SECRET_KEY_2025";

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String internalCall = request.getHeader("X-Internal-Call");
        if ("true".equals(internalCall))
            return true;

        if (request.getDispatcherType() != DispatcherType.REQUEST)
            return true;

        String path = request.getRequestURI();

        if (path.startsWith("/api/auth"))
            return true;

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        try {
            String token = header.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);

            // ADMIN
            if ("ADMIN".equals(role))
                return true;

            // MANAGER
            if ("MANAGER".equals(role)) {
                if (path.startsWith("/api/users/me") ||
                        path.startsWith("/api/users") ||
                        path.contains("/assign") ||
                        path.contains("/parts/approve") ||
                        path.contains("/close") ||
                        path.startsWith("/api/service-requests/") ||
                        path.startsWith("/api/service-requests/manager") ||
                        path.startsWith("/api/technicians") ||
                        path.startsWith("/api/parts") ||
                        path.startsWith("/api/bays") ||
                        path.startsWith("/api/vehicles") ||
                        path.startsWith("/api/invoices"))
                    return true;
            }

            // TECHNICIAN
            if ("TECHNICIAN".equals(role)) {
                if (path.startsWith("/api/users/me") ||
                        path.contains("/start") ||
                        path.contains("/status") ||
                        path.contains("/parts/request") ||
                        path.startsWith("/api/parts") ||
                        path.startsWith("/api/vehicles") ||
                        path.startsWith("/api/service-requests/technician"))
                    return true;
            }

            // CUSTOMER
            if ("CUSTOMER".equals(role)) {
                if (path.equals("/api/service-requests") ||
                        path.startsWith("/api/service-requests/customer") ||
                        path.startsWith("/api/vehicles") ||
                        path.startsWith("/api/invoices/customer") ||
                        path.startsWith("/api/users/me") ||
                        (path.contains("/api/invoices") && path.contains("/pay")) ||
                        path.startsWith("/api/payments") ||
                        (request.getMethod().equals("GET") && path.matches("/api/service-requests/[^/]+"))

                )
                    return true;
            }

            response.setStatus(403);
            return false;

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
