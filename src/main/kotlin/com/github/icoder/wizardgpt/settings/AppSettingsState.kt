package com.github.icoder.wizardgpt.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.net.HttpConfigurable
import com.intellij.util.xmlb.XmlSerializerUtil
import java.net.ProxySelector

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("wizard-gpt-plugin.xml")])
class AppSettingsState : PersistentStateComponent<AppSettingsState?> {
    var apiEnabled: Boolean = false
    var apiKey: String = ""
    var apiModel: String = "text-davinci-003"
    var apiModels: Collection<String> = listOf(apiModel, "text-davinci-002", "text-curie-001", "text-babbage-001", "text-ada-001", "davinci", "curie", "babbage", "ada")
    var temperature: Double = 0.1
    var maxTokens: Int = 1024

    var clientConnectTimeout: Int = 10
    var clientReadTimeout: Int = 20

    var enableCache: Boolean = true
    var cacheInitialCapacity: Int = 64
    var cacheMaximumSize: Int = 1024
    var cacheExpireTimeSec: Int = 20

    var brushReadableEmbeddedPrompt: String = buildString {
        append("- Make the code more readable and accurate after modified.")
        append("\n")
        append("- Keep comments up-to-date and accurate, especially modified the code.")
    }

    var brushRobustEmbeddedPrompt: String = buildString {
        append("- Make the code robust and accurate after modified.")
        append("\n")
        append("- Improve error handling and input validation.")
    }

    var brushCustomEmbeddedPrompt: String = buildString {
        append("- Make the code  accurate after modified.")
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
        val proxySelector: ProxySelector
            get() = ApplicationManager.getApplication().getService(HttpConfigurable::class.java).onlyBySettingsSelector
    }

    override fun getState() = this

}