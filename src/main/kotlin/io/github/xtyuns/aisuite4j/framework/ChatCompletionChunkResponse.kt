package io.github.xtyuns.aisuite4j.framework

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatCompletionChunkResponse(
    val id: String,
    val choices: List<ChunkChoice>,
    val created: Long,
    val model: String,
)

data class ChunkChoice(
    val index: Int,
    val delta: ChoiceMessage,
    @JsonProperty("finish_reason") val finishReason: String?
)