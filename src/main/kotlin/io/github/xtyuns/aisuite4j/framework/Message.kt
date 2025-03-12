package io.github.xtyuns.aisuite4j.framework

import com.fasterxml.jackson.annotation.JsonProperty

data class Message(
    /**
     * @see RoleSpec
     */

    val role: String,
    /**
     * @see TextContentSpec
     * @see ContentPartSpec
     */
    val content: Any?,

    /**
     * @see ToolSpec
     */
    @JsonProperty("tool_call_id")
    val toolCallId: String?,

    /**
     * @see NameSpec
     */
    val name: String? = null,
) {
    enum class Role(val role: String) {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        TOOL("tool")
    }

    companion object : RoleSpec {
        override fun role(role: Role): NameContentToolSpec {
            return MessageCreator.create().role(role)
        }

        override fun role(role: String): NameContentToolSpec {
            return MessageCreator.create().role(role)
        }
    }
}

interface RoleSpec {
    fun role(role: Message.Role): NameContentToolSpec
    fun role(role: String): NameContentToolSpec
}

interface NameSpec {
    fun name(name: String): ContentToolSpec
}

interface TextContentSpec {
    fun text(content: String): EndSpec
}

interface ContentPartSpec {
    fun textPart(text: String): ContentPartEndSpec
    fun imagePart(imageUrl: String): ContentPartEndSpec
}

interface ToolSpec {
    fun toolCallId(toolCallId: String): EndSpec
}

interface EndSpec {
    fun retrieve(): Message
}

interface NameContentToolSpec : NameSpec, TextContentSpec, ContentPartSpec, ToolSpec
interface ContentToolSpec : TextContentSpec, ContentPartSpec, ToolSpec
interface ContentPartEndSpec : ContentPartSpec, EndSpec

class MessageCreator private constructor() : RoleSpec, NameContentToolSpec, ContentToolSpec, ContentPartEndSpec {
    private lateinit var role: String
    private var content: Any? = null
    private var toolCallId: String? = null
    private var name: String? = null

    companion object {
        fun create(): RoleSpec {
            return MessageCreator()
        }
    }

    override fun role(role: Message.Role): NameContentToolSpec {
        this.role = role.role
        return this
    }

    override fun role(role: String): NameContentToolSpec {
        this.role = role
        return this
    }

    override fun name(name: String): ContentToolSpec {
        this.name = name
        return this
    }

    override fun text(content: String): EndSpec {
        this.content = content
        this.toolCallId = null
        return this
    }

    override fun textPart(text: String): ContentPartEndSpec {
        pushOrInitContentParts(
            mapOf(
                "type" to "text",
                "text" to content
            )
        )
        this.toolCallId = null
        return this
    }

    override fun imagePart(imageUrl: String): ContentPartEndSpec {
        pushOrInitContentParts(
            mapOf(
                "type" to "image_url",
                "image_url" to imageUrl
            )
        )
        this.toolCallId = null
        return this
    }

    private fun pushOrInitContentParts(part: Any) {
        if (this.content is List<*>) {
            this.content = this.content as List<*> + part
        } else {
            this.content = listOf(part)
        }
    }

    override fun toolCallId(toolCallId: String): EndSpec {
        this.toolCallId = toolCallId
        this.content = null
        return this
    }


    override fun retrieve(): Message {
        return Message(this.role, this.content, this.toolCallId, this.name)
    }
}
