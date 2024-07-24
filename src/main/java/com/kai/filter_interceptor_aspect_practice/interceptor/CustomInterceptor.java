package com.kai.filter_interceptor_aspect_practice.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomInterceptor implements HandlerInterceptor {

    /*
    preHandle: 在Controller方法執行之前調用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("Start Time: " + System.currentTimeMillis());

        return true; // 返回true表示繼續處理請求,返回false則請求將在此終止
    }

    /*
    postHandle: 在Controller方法執行之後調用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        System.out.println("Request URL: " + request.getRequestURL().toString() +
                " Sent to Handler :: Current Time=" + System.currentTimeMillis());
    }

    /*
    afterCompletion: 在Controller方法執行之後,並且在生成視圖之後調用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        System.out.println("Request URL: " + request.getRequestURL().toString() +
                " :: End Time=" + System.currentTimeMillis());
        System.out.println("Time Taken=" + (System.currentTimeMillis() - startTime));
    }
}
