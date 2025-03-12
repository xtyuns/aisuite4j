package io.github.xtyuns.aisuite4j

import io.github.xtyuns.aisuite4j.framework.Message
import org.junit.jupiter.api.Test

class DeepSeekProviderTest {
    private val token = ""

    @Test
    fun call() {
        val aiClient = AiClientFactory.create("deepseek-chat", token)
        val chatCompletionResponse =
            aiClient.call(listOf(Message.role(Message.Role.USER).text("hi").retrieve()))
        println(chatCompletionResponse)
    }


    @Test
    fun stream() {
        val aiClient = AiClientFactory.create("deepseek-chat", token)
        val chatCompletionResponse = aiClient.stream(listOf(Message.role(Message.Role.USER).text("hi").retrieve()))
        chatCompletionResponse.doOnNext {
            println(it)
        }.blockLast()
    }
}