# Springboot Fileter VS Interceptor VS Aspect

很久之前其實有大概寫過 Aspect，但也都忘光光了，這兩天專案上遇到要在很多地方處理 JWT，我提出 Filter 跟 Interceptor 都可以使用建議。這時我就想，這兩個東西都可以達到目的，那他們的差別到底是啥啊?

查一查發現，對吼，我還有 Aspect 也可以達到一樣的目的。

那到底，這三種到底有甚麼差別?

## 建立專案

來建立一個專案，後面把三個主題都動手寫寫看。

### 環境
 
- Project: maven
- Java 17
- spring boot 3.3.2
- Dependencies: 
  - Spring Web
  - Spring Boot DevTools
  - Lombok
  - org.springdoc 2.6.0

- 建立一個檔案 config/OpenApiConfig.java

```java
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Practice Filete, Interceptor, Aspect",
                version = "1.0"
        )
)
public class OpenApiConfig {
}
```

- 接下來就來實做吧

## 三者的介紹

### Filter

Filter 是 Servlet 的一部分，它可以在請求進入 Servlet 之前，以及響應返回之前，對請求進行處理。
也就是到達 Controller 及離開 Controller 之後，Filter 都可以進行處理。

通常我們會用來執行一些通用的任務，例如日誌記錄、字符編碼轉換、驗證登錄用戶身份、數據篩選等。

接下來我們會用 Spring 提供的 `OncePerRequestFilter` 來實現 Filter。

當收到請求時，會經過 `doFilterInternal`，參數包含一個 `FilterChain`，他會將所有現有的`Filter`串起來，進入到 controller 時以升序執行，離開 controller 時以降序執行。

1. 先建立一個 `FilterController`
```java
@RestController
public class FilterController {

    @GetMapping("/hello")
    @Operation(summary = "Hello")
    public String hello() {
        System.out.println("hello in FilterController");
        return "hello";
    }

}
```
2. 建立第一個 `FirstFilter`
```java
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
```
3. 建立第二個 `SecondFilter`
```java
public class SecondFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("SecondFilter: Request URL before filterChain.doFilter: " + request.getRequestURL() + " Time: " + startTime + "ms");
        filterChain.doFilter(request, response);
        long endTime = System.currentTimeMillis();
        System.out.println("SecondFilter: Request URL after filterChain.doFilter: " + request.getRequestURL() + " Time: " + endTime + "ms");
    }
}
```
4. 建立 `FilterConfig`
```java
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
```
5. 啟動專案，訪問 `http://localhost:8080/hello`，可以看到 console 有以下輸出
```
FirstFilter: Request URL before filterChain.doFilter: http://localhost:8080/hello Time: 1721801428816ms
SecondFilter: Request URL before filterChain.doFilter: http://localhost:8080/hello Time: 1721801428816ms
hello in FilterController
SecondFilter: Request URL after filterChain.doFilter: http://localhost:8080/hello Time: 1721801428818ms
FirstFilter: Request URL after filterChain.doFilter: http://localhost:8080/hello Time: 1721801428818ms
```

- 我們可以觀察到，`FirstFilter` 在 `SecondFilter` 之前執行，而在離開 Controller 時，`SecondFilter` 在 `FirstFilter` 之前執行。

### Interceptor

### Aspect

## 差別整理


## 使用情境


## 使用範例


## 結論

