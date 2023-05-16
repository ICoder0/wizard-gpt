package com.github.icoder.wizardgpt.actions

import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.intellij.lang.Language

class GptCodeRobustIntentionAction : GptCodeBrushIntentionAction() {
    override fun getText() = "Brush ROBUST"
    override fun getFamilyName() = text

    override fun wrapPrompt(code: String, language: Language): String = buildString {
        append("Act as ${language.id} Programmer")
        append("\n")
        append(AppSettingsState.instance.brushRobustEmbeddedPrompt)
        append("\n")
        append("### Origin")
        append("\n")
        append(code)
        append("\n")
        append("### Modified")
    }
}