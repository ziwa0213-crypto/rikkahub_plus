package me.rerere.ai.provider.providers.openai

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rerere.ai.core.MessageRole
import me.rerere.ai.core.Tool
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ModelAbility
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.provider.TextGenerationParams
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.ui.UIMessagePart
import me.rerere.ai.util.KeyRoulette
import okhttp3.OkHttpClient
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatCompletionsDeepSeekReasoningTest {

    private lateinit var api: ChatCompletionsAPI

    @Before
    fun setUp() {
        api = ChatCompletionsAPI(OkHttpClient(), KeyRoulette.default())
    }

    @Test
    fun `deepseek host forces reasoning history when tools are sent`() {
        val body = buildRequest(
            baseUrl = "https://api.deepseek.com/v1",
            modelId = "thinking-model",
            includeHistoryReasoning = false,
        )

        assertTrue(assistantMessages(body).single().jsonObject.containsKey("reasoning_content"))
    }

    @Test
    fun `deepseek model forces reasoning history through a third party host`() {
        val body = buildRequest(
            baseUrl = "https://proxy.example.com/v1",
            modelId = "deepseek-v4-flash",
            includeHistoryReasoning = false,
        )

        assertTrue(assistantMessages(body).single().jsonObject.containsKey("reasoning_content"))
    }

    @Test
    fun `non deepseek providers keep the history reasoning switch behavior`() {
        val body = buildRequest(
            baseUrl = "https://api.example.com/v1",
            modelId = "thinking-model",
            includeHistoryReasoning = false,
        )

        assertFalse(assistantMessages(body).single().jsonObject.containsKey("reasoning_content"))
    }

    @Test
    fun `deepseek without tools does not force reasoning history`() {
        val body = buildRequest(
            baseUrl = "https://api.deepseek.com/v1",
            modelId = "deepseek-v4-flash",
            includeHistoryReasoning = false,
            withTools = false,
        )

        assertFalse(assistantMessages(body).single().jsonObject.containsKey("reasoning_content"))
    }

    private fun buildRequest(
        baseUrl: String,
        modelId: String,
        includeHistoryReasoning: Boolean,
        withTools: Boolean = true,
    ): JsonObject {
        val method = ChatCompletionsAPI::class.java.getDeclaredMethod(
            "buildChatCompletionRequest",
            List::class.java,
            TextGenerationParams::class.java,
            ProviderSetting.OpenAI::class.java,
            Boolean::class.javaPrimitiveType,
        )
        method.isAccessible = true

        return method.invoke(
            api,
            listOf(
                UIMessage.user("Use a tool."),
                UIMessage(
                    role = MessageRole.ASSISTANT,
                    parts = listOf(
                        UIMessagePart.Reasoning(reasoning = "Historical reasoning"),
                        UIMessagePart.Text("I will use a tool."),
                    ),
                ),
            ),
            TextGenerationParams(
                model = Model(
                    modelId = modelId,
                    abilities = if (withTools) listOf(ModelAbility.TOOL) else emptyList(),
                ),
                tools = if (withTools) listOf(testTool()) else emptyList(),
            ),
            ProviderSetting.OpenAI(
                baseUrl = baseUrl,
                includeHistoryReasoning = includeHistoryReasoning,
            ),
            false,
        ) as JsonObject
    }

    private fun assistantMessages(body: JsonObject) = body["messages"]!!
        .jsonArray
        .filter { it.jsonObject["role"]?.jsonPrimitive?.content == "assistant" }

    private fun testTool() = Tool(
        name = "test_tool",
        description = "A test tool.",
        execute = { emptyList() },
    )
}
