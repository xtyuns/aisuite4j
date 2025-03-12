package io.github.xtyuns.aisuite4j.providers

import io.github.xtyuns.aisuite4j.AiClient
import io.github.xtyuns.aisuite4j.ClientProvider

class DashScopeProvider : ClientProvider {
    override fun createClient(model: String, token: String, defaultOptions: Map<String, Any>): AiClient {
        return DashScopeAiClient(token, defaultOptions + mapOf("model" to model))
    }

    override fun supportModels(): Set<String> {
        return setOf("qwen-max", "qwen-plus", "qwen-turbo", "qwen-long", "qwq-32b")
    }
}

class DashScopeAiClient(
    token: String,
    defaultOptions: Map<String, Any>
) : AbstractAiClient("https://dashscope.aliyuncs.com/compatible-mode/v1", "/chat/completions", token, defaultOptions)