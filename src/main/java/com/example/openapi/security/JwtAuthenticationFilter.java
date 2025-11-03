package com.example.openapi.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String p = req.getServletPath();
        if (p.startsWith("/auth") || p.equals("/") || p.equals("/about") || p.equals("/login")
                || p.startsWith("/css/") || p.startsWith("/img/")
                || p.startsWith("/js/") || p.startsWith("/webjars/")) {
            chain.doFilter(req, res);
            return;
        }
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            if (req.getCookies() != null) {
                for (Cookie c : req.getCookies()) {
                    if ("AUTH".equals(c.getName())) {
                        header = "Bearer " + c.getValue();
                        break;
                    }
                }
            }
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }
}
