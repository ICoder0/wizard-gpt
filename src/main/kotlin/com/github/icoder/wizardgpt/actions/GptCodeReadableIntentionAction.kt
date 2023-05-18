package com.github.icoder.wizardgpt.actions

import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.intellij.lang.Language

class GptCodeReadableIntentionAction : GptCodeBrushIntentionAction() {
    override fun getText() = "Brush READABLE"
    override fun getFamilyName() = text

    override fun wrapPrompt(code: String, language: Language): String = buildString {
        append("Act as ${language.id} Programmer")
        append("\n")

        append(AppSettingsState.instance.brushReadableEmbeddedPrompt)
        append("\n")
        append("### Origin")
        append("\n")
        append(code)
        append("\n")
        append("### Modified")
    }
}