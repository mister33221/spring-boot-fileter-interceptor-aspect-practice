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

Filter 能拿到 HTTP 請求：Filter 作為 Servlet 規範的一部分，可以訪問原始的 HTTP 請求和響應。
但拿不到處理請求方法的信息：Filter 在 Spring MVC 的處理流程之前執行，因此它不知道哪個控制器方法將處理這個請求。

例如：
```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    // 可以訪問 URL、headers、parameters 等
    String url = httpRequest.getRequestURL().toString();
    // 但不知道哪個控制器方法會處理這個請求
}
```

通常我們會用來執行一些通用的任務，例如日誌記錄、字符編碼轉換、驗證登錄用戶身份、數據篩選等。

#### 實作

接下來我們會用 Spring 提供的 `OncePerRequestFilter` 來實現 Filter。

當收到請求時，會經過 `doFilterInternal`，參數包含一個 `FilterChain`，他會將所有現有的`Filter`串起來，進入到 controller 時以升序執行，離開 controller 時以降序執行。

1. 先建立一個 `FilterTestController`
```java
@RestController
@RequestMapping("/filter")
public class FilterTestController {

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
    bean.addUrlPatterns("/filter/*"); // 設定 Filter 的 URL Pattern，只有符合這個 Pattern 的 URL 才會經過這個 Filter
    bean.setName("firstFilter"); // 設定 Filter 的名稱，要注意不要跟其他 Filter 重複
    bean.setOrder(1); // 設定 Filter 的執行順序
    return bean;
  }

  @Bean
  public FilterRegistrationBean SecondFilter() {
    FilterRegistrationBean<SecondFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new SecondFilter());
    bean.addUrlPatterns("/filter/*"); // 設定 Filter 的 URL Pattern，只有符合這個 Pattern 的 URL 才會經過這個 Filter
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

它可以在Controller處理請求之前、之後,以及完成視圖渲染之後執行特定的邏輯。

Interceptor的主要特點:

- 可以訪問請求和響應對象
- 可以決定請求是否繼續到達Controller
- 可以在請求處理完成後執行一些後續操作
- 可以應用於特定的URL模式

能拿到 HTTP 請求信息：Interceptor 可以訪問當前的 HTTP 請求和響應。
能拿到處理請求方法的信息：Interceptor 知道哪個控制器方法將被調用。
拿不到方法的參數信息：雖然知道方法，但不能直接訪問傳遞給該方法的實際參數值。

例如：
```java
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // 可以訪問 HTTP 請求信息
    String url = request.getRequestURL().toString();
    
    if (handler instanceof HandlerMethod) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 可以獲取將被調用的控制器方法信息
        String controllerName = handlerMethod.getBeanType().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        // 但不能直接獲取方法的實際參數值
    }
    return true;
}
```

接下來在回到我們的專案中來實作它吧。

#### 實作

1. 建立一個 `InterceptorTestController`
```java
@RestController
@RequestMapping("/interceptor")
public class InterceptorTestController {

  @GetMapping("/hello")
  public String hello() {
    System.out.println("hello in InterceptorController");
    return "hello";
  }

}
```
2. 建立一個 `CustomInterceptor`
```java
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
```
3. 建立 `WebMvcConfig`
```java
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
```
4. 啟動專案，訪問 `http://localhost:8080/interceptor/hello`，可以看到 console 有以下輸出
```
Request URL: http://localhost:8080/interceptor/hello
Start Time: 1721805129840
hello in InterceptorController
Request URL: http://localhost:8080/interceptor/hello Sent to Handler :: Current Time=1721805129843
Request URL: http://localhost:8080/interceptor/hello :: End Time=1721805129843
Time Taken=3
```

### Aspect

能拿到方法的參數信息：Aspect 可以直接訪問被切入方法的參數。
拿不到 HTTP 請求信息：Aspect 作用於方法級別，默認情況下不直接與 HTTP 層交互。

例如：
```java
@Around("execution(* com.example.controller.*.*(..))")
public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    // 可以訪問方法參數
    Object[] args = joinPoint.getArgs();
    String methodName = joinPoint.getSignature().getName();
    
    // 但不能直接訪問 HTTP 請求信息
    // 如果需要，可以將 HttpServletRequest 作為參數傳遞給方法
    
    return joinPoint.proceed();
}
```

程式碼範例之前寫過了，我就不在這邊再寫一次了。
有興趣的話，可以來[這邊](https://github.com/mister33221/AOP-example.git)看看。

## 差別整理

![img.png](img.png)

| Filter                                        | Interceptor                                                      | 摘要                                         |
|-----------------------------------------------|------------------------------------------------------------------|--------------------------------------------|
| Filter 接口定義在 `javax.servlet` 包中               | 接口 `HandlerInterceptor` 定義在 `org.springframework.web.servlet` 包中 |                                            |
| Filter 在 `web.xml` 中定義                        |                                                                  |                                            |
| Filter 只在 Servlet 前後起作用。                      | 拦截器能夠深入到方法前後、異常拋出前後，具有更大的彈性。                                     | 在 Spring 框架中，優先使用拦截器。拦截器能輕鬆實現 Filter 的所有功能。 |
| Filter 是 Servlet 規範中定義的。                      | 拦截器既可用於 Web 程式，也可用於 Application、Swing 程式中。                       | 使用範圍不同                                     |
| Filter 是在 Servlet 規範中定義的，由 Servlet 容器支持。      | 拦截器是在 Spring 容器內，由 Spring 框架支持。                                  | 規範不同                                       |
| Filter 不能使用 Spring 容器資源                       | 拦截器是 Spring 組件，能使用 Spring 里的任何資源，通過 IoC 注入。                      | 在 Spring 中使用拦截器更容易                         |
| Filter 由 Server（如 Tomcat）調用                   | Interceptor 由 Spring 調用                                          | 因此 Filter 總是優先於 Interceptor 執行             |

## 使用情境


## 使用範例


## 結論

