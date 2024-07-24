package com.kai.filter_interceptor_aspect_practice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FirstFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("FirstFilter: Request URL before filterChain.doFilter: " + request.getRequestURL() + " Time: " + startTime + "ms");
        filterChain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        System.out.println("FirstFilter: Request URL after filterChain.doFilter: " + request.getRequestURL() + " Time: " + endTime + "ms");
    }
}
