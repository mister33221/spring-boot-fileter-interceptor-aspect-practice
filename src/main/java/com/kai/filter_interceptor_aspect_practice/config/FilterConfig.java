package com.kai.filter_interceptor_aspect_practice.config;

import com.kai.filter_interceptor_aspect_practice.filter.LogTimeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean logProcessTimeFilter() {
        FilterRegistrationBean<LogTimeFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LogTimeFilter());
        bean.addUrlPatterns("/hello");
        bean.setName("logProcessTimeFilter");

        return bean;
    }

}
