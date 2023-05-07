//package com.github.bofa1ex.wizardgpt.actions
//
//import com.intellij.lang.Language
//import com.intellij.openapi.project.*
//
//class GptCodeReviewIntentionAction : GptCodeBrushIntentionAction() {
//    override fun getText() = "Brush CODE_REVIEW"
//
//    override fun wrapPrompt(language: Language): String = """
//        Language: ${language.id}
//        ###
//        Code
//    """.trimIndent()
//
//    override fun invokeGpt(project: Project, code: String, prompt: String, startOffset: Int, endOffset: Int): String? {
//        TODO("Not yet implemented")
//    }
//}