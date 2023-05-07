package com.github.icoder.wizardgpt.actions.lang

import com.google.common.collect.ImmutableMap
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import java.util.function.Function
import java.util.stream.Collectors

interface ElementTypeMatcher {
    fun isStatement(ele: PsiElement): Boolean
    fun isStatement(start: PsiElement, end: PsiElement, parent: PsiElement): Boolean

    fun isCodeBlock(ele: PsiElement): Boolean
    fun isCodeBlock(start: PsiElement, end: PsiElement, parent: PsiElement): Boolean

    companion object {
        private val ELEMENT_TYPE_NAME_MATCHER_MAP: Map<String, ElementTypeMatcher> = ImmutableMap.of<String, ElementTypeMatcher>(
            "JAVA", JavaElementTypeMatcher()
        )

        val INSTANCE: Map<Language, ElementTypeMatcher> = Language.getRegisteredLanguages()
            .stream()
            .filter { ELEMENT_TYPE_NAME_MATCHER_MAP.containsKey(it.id) }
            .collect(Collectors.toMap(Function.identity()) { ELEMENT_TYPE_NAME_MATCHER_MAP.getValue(it.id) })
    }
}