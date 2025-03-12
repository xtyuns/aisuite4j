package io.github.xtyuns.aisuite4j

interface ClientProvider {
    fun createClient(model: String, token: String, defaultOptions: Map<String, Any> = emptyMap()): AiClient

    fun supportModels(): Set<String>
}