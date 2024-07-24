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



### Interceptor

### Aspect

## 差別整理


## 使用情境


## 使用範例


## 結論

