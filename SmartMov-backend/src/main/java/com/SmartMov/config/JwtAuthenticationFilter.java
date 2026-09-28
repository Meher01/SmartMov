package com.SmartMov.config;

import com.SmartMov.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null
            || !authorizationHeader.startsWith("Bearer ")) {

        filterChain.doFilter(request, response);
        return;
    }

    String token = authorizationHeader.substring(7);

    if (!jwtService.isTokenValid(token)) {
        filterChain.doFilter(request, response);
        return;
    }

    String username = jwtService.extractUsername(token);

    UserDetails principal = User.withUsername(username)
            .password("")
            .authorities("ROLE_USER")
            .build();

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities());

    authentication.setDetails(
            new WebAuthenticationDetailsSource()
                    .buildDetails(request));

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    filterChain.doFilter(request, response);
}
}