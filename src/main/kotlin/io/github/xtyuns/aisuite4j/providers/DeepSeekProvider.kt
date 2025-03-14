package io.github.xtyuns.aisuite4j.providers

import io.github.xtyuns.aisuite4j.AiClient
import io.github.xtyuns.aisuite4j.ClientProvider

class DeepSeekProvider : ClientProvider {
    override fun createClient(model: String, token: String, defaultOptions: Map<String, Any>): AiClient {
        return DeepSeekAiClient(token, defaultOptions + mapOf("model" to model))
    }

    override fun supportModels(): Set<String> {
        return setOf("deepseek-chat", "deepseek-reasoner")
    }
}

class DeepSeekAiClient(
    token: String,
    defaultOptions: Map<String, Any>
) : AbstractAiClient("https://api.deepseek.com", "/chat/completions", token, defaultOptions)