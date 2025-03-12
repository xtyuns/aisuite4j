# aisuite4j

提供简单、统一的方式调用多种 LLM 供应商的接口, 参考项目: [aisuite](https://github.com/andrewyng/aisuite)

## 使用方法

### 流式调用

```kotlin
val aiClient = AiClientFactory.create("deepseek-chat", token)
val chatCompletionResponse = aiClient.stream(listOf(Message.role(Message.Role.USER).text("hi").retrieve()))
chatCompletionResponse.doOnNext {
    println(it)
}.blockLast()
```

### 同步调用

```kotlin
val aiClient = AiClientFactory.create("qwq-32b", token)
val chatCompletionResponse = aiClient.call(listOf(Message.role(Message.Role.USER).text("hi").retrieve()))
println(chatCompletionResponse)
```