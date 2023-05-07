package com.github.icoder.wizardgpt.util

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.github.icoder.wizardgpt.util.Openai.cache
import com.google.gson.JsonObject
import groovy.transform.EqualsAndHashCode
import okhttp3.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.time.Duration
import java.util.*

interface OpenaiService {

    @POST("/v1/completions")
    fun _completion(
        @Body request: CompletionRequest,
        @Header("Authorization") apiKey: String = "Bearer ${AppSettingsState.instance.apiKey}"
    ): Call<ChoiceResponse>

    @GET("/v1/models")
    fun _models(@Header("Authorization") apiKey: String): Call<JsonObject>

    @EqualsAndHashCode
    data class CompletionRequest(
        val prompt: String,
        val model: String = AppSettingsState.instance.apiModel,
        val max_tokens: Int = AppSettingsState.instance.maxTokens,
        val temperature: Double = AppSettingsState.instance.temperature,
        val stop: String = ""
    ) : OpenAiRequest

    data class ChoiceResponse(
        val choices: List<Choice>,
    ) : OpenAiResponse

    data class Choice(
        val text: String,
        val index: Int,
        val logprobs: Any,
        val finish_reason: String,
    )
}


fun OpenaiService.models(apiKey: String): Call<JsonObject> = _models("Bearer $apiKey")

fun OpenaiService.completion(request: OpenaiService.CompletionRequest): OpenaiService.ChoiceResponse? {
    val choiceResponse = cache.getIfPresent(request)
    if (choiceResponse != null) return choiceResponse as OpenaiService.ChoiceResponse
    val response: Response<OpenaiService.ChoiceResponse> = _completion(request).execute()

    if (response.isSuccessful.not()) throw IllegalArgumentException("completion response error, status: ${response.code()}, message: ${response.errorBody()?.string()}")
    return response.body().also { cache.put(request, it) }
}

object Openai {
    var instance: OpenaiService = buildClient()
    var cache: Cache<OpenAiRequest, OpenAiResponse> = buildCache()

    fun buildCache(): Cache<OpenAiRequest, OpenAiResponse> = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofSeconds(AppSettingsState.instance.cacheExpireTimeSec.toLong()))
        .initialCapacity(AppSettingsState.instance.cacheInitialCapacity)
        .maximumSize(AppSettingsState.instance.cacheMaximumSize.toLong())
        .build()

    fun buildClient(): OpenaiService = Retrofit.Builder()
        .baseUrl("https://api.openai.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(AppSettingsState.instance.clientConnectTimeout.toLong()))
                .readTimeout(Duration.ofSeconds(AppSettingsState.instance.clientReadTimeout.toLong()))
                .proxySelector(AppSettingsState.proxySelector)
                .build()
        )
        .build()
        .create(OpenaiService::class.java)
}

interface OpenAiRequest
interface OpenAiResponse