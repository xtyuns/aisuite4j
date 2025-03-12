package io.github.xtyuns.aisuite4j.providers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.xtyuns.aisuite4j.AiClient
import io.github.xtyuns.aisuite4j.framework.ChatCompletionChunkResponse
import io.github.xtyuns.aisuite4j.framework.ChatCompletionResponse
import io.github.xtyuns.aisuite4j.framework.Message
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class AbstractAiClient(
    private val baseUrl: String,
    private val completionsPath: String,
    private val token: String,
    private val defaultOptions: Map<String, Any>
) : AiClient {
    private val objectMapper = ObjectMapper().also {
        it.registerKotlinModule()
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private val webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", "Bearer $token")
        .build()

    override fun stream(messages: List<Message>, options: Map<String, Any>): Flux<ChatCompletionChunkResponse> {
        val bodyValue = buildBodyValue(options, messages, true)

        val chunkFlux = webClient.post().uri(this.completionsPath)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(bodyValue))
            .retrieve()
            .bodyToFlux(String::class.java)

        return transformChunkFlux(chunkFlux)
    }

    override fun call(messages: List<Message>, options: Map<String, Any>): ChatCompletionResponse {
        val bodyValue = buildBodyValue(options, messages, false)

        val chatMono = webClient.post().uri(this.completionsPath)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(bodyValue))
            .retrieve()
            .bodyToMono(String::class.java)

        return transformChatMono(chatMono)
    }

    protected open fun buildBodyValue(
        options: Map<String, Any>,
        messages: List<Message>,
        stream: Boolean
    ): Map<String, Any> {
        return this.defaultOptions + options + mapOf(
            "messages" to messages,
            "stream" to stream
        )
    }

    protected open fun transformChunkFlux(chunkFlux: Flux<String>): Flux<ChatCompletionChunkResponse> {
        return chunkFlux.filter {
            it != "[DONE]"
        }.map {
            objectMapper.readValue(it, ChatCompletionChunkResponse::class.java)
        }
    }

    protected open fun transformChatMono(chatMono: Mono<String>): ChatCompletionResponse {
        return chatMono.map {
            objectMapper.readValue(it, ChatCompletionResponse::class.java)
        }.block()!!
    }
}