package io.github.xtyuns.aisuite4j

import io.github.xtyuns.aisuite4j.framework.ChatCompletionChunkResponse
import io.github.xtyuns.aisuite4j.framework.ChatCompletionResponse
import io.github.xtyuns.aisuite4j.framework.Message
import reactor.core.publisher.Flux

interface AiClient {
    fun stream(messages: List<Message>, options: Map<String, Any> = emptyMap()): Flux<ChatCompletionChunkResponse>

    fun call(messages: List<Message>, options: Map<String, Any> = emptyMap()): ChatCompletionResponse
}