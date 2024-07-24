package com.kai.filter_interceptor_aspect_practice.config;

import com.kai.filter_interceptor_aspect_practice.interceptor.CustomInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomInterceptor customInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customInterceptor)
                .addPathPatterns("/interceptor/*");  // 應用於所有URL
//                .excludePathPatterns("/admin/**"); // 排除admin路徑
    }
}
