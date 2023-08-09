package com.github.icoder.wizardgpt.settings

import com.github.icoder.wizardgpt.WizardGptBundle
import com.github.icoder.wizardgpt.util.Openai
import com.github.icoder.wizardgpt.util.WizardGptTopics
import com.github.icoder.wizardgpt.util.models
import com.intellij.application.options.editor.CheckboxDescriptor
import com.intellij.application.options.editor.checkBox
import com.intellij.ide.IdeBundle
import com.intellij.openapi.application.ApplicationBundle
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.PropertyBinding
import com.intellij.ui.layout.listCellRenderer
import com.intellij.ui.layout.selected
import java.awt.event.ActionEvent
import java.util.concurrent.atomic.AtomicReference

class AppSettingsConfigurable : BoundConfigurable(WizardGptBundle.message("name")), Configurable.NoScroll {
    private var cacheOptionsModified: Boolean = false
    private var clientOptionsModified: Boolean = false
    private var checkBoxEnableCheckBox = JBCheckBox(WizardGptBundle.message("enableCacheLabel"))
    private var apiKeyTextField = JBTextField()
    private val model: EditorSettingsExternalizable
        get() = EditorSettingsExternalizable.getInstance()
    private val myShowIntentionPreviewCheckBox
        get() = CheckboxDescriptor(
            ApplicationBundle.message("checkbox.show.intention.preview"), PropertyBinding(
                model::isShowIntentionPreview, model::setShowIntentionPreview
            )
        )

    override fun apply() {
        super.apply()
        checkApiKeyValid(hiddenProcess = true)
        WizardGptTopics.publish()
        if (cacheOptionsModified) {
            Openai.cache = Openai.buildCache()
            cacheOptionsModified = false
        }
        if (clientOptionsModified) {
            Openai.instance = Openai.buildClient()
            clientOptionsModified = false
        }
    }

    @Suppress("DialogTitleCapitalization")
    private fun checkApiKeyValid(@Suppress("UNUSED_PARAMETER") e: ActionEvent? = null, hiddenProcess: Boolean = false) {
        val apiKey = apiKeyTextField.text
        if (apiKey.isBlank()) {
            AppSettingsState.instance.apiEnabled = false
            if (hiddenProcess.not()) Messages.showWarningDialog("Api key must not be blank.", "API Settings")
            return
        }

        val exceptionReference = AtomicReference<Exception>()
        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            try {
                val response = Openai.instance.models(apiKey).execute()
                if (response.isSuccessful.not()) exceptionReference.set(
                    IllegalAccessException("${response.code()}\n${response.errorBody()?.string()}")
                )
            } catch (e: Exception) {
                exceptionReference.set(e)
            }
        }, IdeBundle.message("progress.title.check.connection"), true, null)
        val exception = exceptionReference.get()
        if (exception == null) {
            AppSettingsState.instance.apiEnabled = true
            if (hiddenProcess.not()) Messages.showInfoMessage("Connection successful", "API settings")
        } else {
            AppSettingsState.instance.apiEnabled = false
            if (hiddenProcess.not()) Messages.showErrorDialog("Error: ${exception.message}", "API settings")
        }
    }

    @Suppress("DialogTitleCapitalization")
    override fun createPanel(): DialogPanel = panel {
        group("Required") {
            row("API key") {
                cell(apiKeyTextField).columns(COLUMNS_MEDIUM).bindText(AppSettingsState.instance::apiKey)
                button("Check", ::checkApiKeyValid)
                browserLink("Generate", "https://beta.openai.com/account/api-keys")
            }
            row("Model") {
                comboBox(AppSettingsState.instance.apiModels, listCellRenderer { value, _, _ -> setText(value) })
                    .bindItem(AppSettingsState.instance::apiModel.toNullableProperty())
                    .applyToComponent() { isEditable = true }
                browserLink("Overview", "https://platform.openai.com/docs/models/overview")
            }
            row {
                checkBox(myShowIntentionPreviewCheckBox)
            }
        }
        collapsibleGroup("Client Options") {
            row {
                intTextField(-1..Int.MAX_VALUE).label("ConnectTimeout(sec)")
                    .comment("range = -1..+inf, step = 1")
                    .bindIntText(AppSettingsState.instance::clientConnectTimeout)
                    .onApply { clientOptionsModified = true }
                intTextField(-1..Int.MAX_VALUE).label("ReadTimeout(sec)")
                    .comment("range = -1..+inf, step = 1")
                    .bindIntText(AppSettingsState.instance::clientReadTimeout)
                    .onApply { clientOptionsModified = true }
            }
            row {
                cell(checkBoxEnableCheckBox)
                    .comment(WizardGptBundle.message("enableCacheComment"), 100)
                    .bindSelected(AppSettingsState.instance::enableCache)
                    .onApply { cacheOptionsModified = true }
            }
            row {
                intTextField(1..512, keyboardStep = 1).label("Minimum")
                    .comment("range = 1..512, step = 1")
                    .bindIntText(AppSettingsState.instance::cacheInitialCapacity)
                    .onApply { cacheOptionsModified = true }
                intTextField(16..4096, keyboardStep = 1).label("Maximum")
                    .comment("range = 16..4096, step = 1")
                    .bindIntText(AppSettingsState.instance::cacheMaximumSize)
                    .onApply { cacheOptionsModified = true }
                spinner(5..300, step = 1).label("Expire(sec)")
                    .comment("range = 5..300, step = 1")
                    .bindIntValue(AppSettingsState.instance::cacheExpireTimeSec)
                    .onApply { cacheOptionsModified = true }
            }.enabledIf(checkBoxEnableCheckBox.selected)
        }
        collapsibleGroup("Completion Options") {
            row {
                intTextField(128..Int.MAX_VALUE, keyboardStep = 1).label("Max tokens")
                    .comment("128..+inf")
                    .bindIntText(AppSettingsState.instance::maxTokens)
                spinner(0.0..1.0, step = 0.1).label("Temperature")
                    .comment("range = 0.1..1.0, step = 0.1")
                    .bindValue(AppSettingsState.instance::temperature)
            }
        }
    }
}