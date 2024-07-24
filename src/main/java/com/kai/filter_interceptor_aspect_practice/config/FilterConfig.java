package com.kai.filter_interceptor_aspect_practice.config;

import com.kai.filter_interceptor_aspect_practice.filter.FirstFilter;
import com.kai.filter_interceptor_aspect_practice.filter.SecondFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean FirstFilter() {
        FilterRegistrationBean<FirstFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new FirstFilter());
        bean.addUrlPatterns("/hello"); // 設定 Filter 的 URL Pattern，只有符合這個 Pattern 的 URL 才會經過這個 Filter
        bean.setName("firstFilter"); // 設定 Filter 的名稱，要注意不要跟其他 Filter 重複
        bean.setOrder(1); // 設定 Filter 的執行順序
        return bean;
    }

    @Bean
    public FilterRegistrationBean SecondFilter() {
        FilterRegistrationBean<SecondFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new SecondFilter());
        bean.addUrlPatterns("/hello"); // 設定 Filter 的 URL Pattern，只有符合這個 Pattern 的 URL 才會經過這個 Filter
        bean.setName("secondFilter"); // 設定 Filter 的名稱，要注意不要跟其他 Filter 重複
        bean.setOrder(2); // 設定 Filter 的執行順序
        return bean;
    }

}
