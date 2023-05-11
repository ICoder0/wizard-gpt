package com.github.icoder.wizardgpt.actions

import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.github.icoder.wizardgpt.util.Openai
import com.github.icoder.wizardgpt.util.OpenaiService
import com.github.icoder.wizardgpt.util.completion
import com.intellij.lang.Language
import com.intellij.openapi.project.Project

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

    override fun invokeGpt(project: Project, prompt: String): String? =
        Openai.instance.completion(OpenaiService.CompletionRequest(prompt, stop = "###"))
            ?.choices
            ?.firstOrNull()
            ?.text
            ?.trim('\n')
}