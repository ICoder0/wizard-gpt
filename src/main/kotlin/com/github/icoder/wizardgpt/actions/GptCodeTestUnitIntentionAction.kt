package com.github.icoder.wizardgpt.actions

import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class GptCodeTestUnitIntentionAction : GptCodeBrushIntentionAction() {
    override fun getText() = "Brush TEST"
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

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
//        val popup =
//            JBPopupFactory.getInstance()
//                .createComponentPopupBuilder(textField, textField.getPreferredFocusedComponent())
//                .setRequestFocus(true)
//                .setFocusable(true)
//                .setShowShadow(true)
//                .createPopup()
//        val dimension = popup.content.preferredSize
//        val at = Point(0, -dimension.height)
//

//        LanguageTestCreators.INSTANCE.forLanguage(element.language)
//            .createTest(project, editor, element.containingFile)
//
//        val frameworks: MutableSet<TestFramework> = LinkedHashSet()
//        for (framework in TestFramework.EXTENSION_NAME.extensionList) {
//            if (framework.isTestClass(psiClass)) {
//                frameworks.add(framework)
//            }
//        }
//
//        for (framework in TestFramework.EXTENSION_NAME.extensionList) {
//            if (frameworks.contains(framework)) continue
//            if (framework.findSetUpMethod(psiClass) != null || framework.findTearDownMethod(psiClass) != null) {
//                frameworks.add(framework)
//            }
//        }
    }
}