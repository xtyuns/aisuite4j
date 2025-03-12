package io.github.xtyuns.aisuite4j

import java.util.ServiceLoader

object AiClientFactory {
    fun create(model: String, token: String, defaultOptions: Map<String, Any> = emptyMap()): AiClient {
        val clientProvider = ServiceLoader.load(ClientProvider::class.java).find {
            it.supportModels().contains(model)
        } ?: throw IllegalArgumentException("model $model is not found")
        return clientProvider.createClient(model, token, defaultOptions)
    }
}