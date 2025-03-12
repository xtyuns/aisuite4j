package io.github.xtyuns.aisuite4j.framework

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
)

data class Choice(
    val index: Int,
    val message: ChoiceMessage,
    @JsonProperty("finish_reason") val finishReason: String
)

data class ChoiceMessage(
    val content: String?,
    val role: String = Message.Role.ASSISTANT.role,
    @JsonProperty("reasoning_content") val reasoningContent: String?
)