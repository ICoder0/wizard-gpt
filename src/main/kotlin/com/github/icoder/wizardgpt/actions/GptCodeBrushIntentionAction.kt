package com.github.icoder.wizardgpt.actions

import com.github.icoder.wizardgpt.WizardGptBundle
import com.github.icoder.wizardgpt.actions.lang.ElementTypeMatcher
import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.github.icoder.wizardgpt.util.WizardGptNotifier
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.lang.LanguageNamesValidation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.ImaginaryEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.applyIf
import javax.swing.Icon

abstract class GptCodeBrushIntentionAction : Iconable, LowPriorityAction, PsiElementBaseIntentionAction() {
    abstract fun wrapPrompt(code: String, language: Language): String
    abstract fun invokeGpt(project: Project, prompt: String): String?
    private fun isPreviewEditor(editor: Editor) = editor is ImaginaryEditor

    final override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        if (ApplicationManager.getApplication().assertReadAccessAllowed().runCatching { -> }.isFailure) return false
        if (canModify(element).not()) return false

        val (selectedText, selectedRange) = editor.selectionModel.let {
            it.selectedText to TextRange.create(it.selectionStart, it.selectionEnd)
        }

        if (selectedText.isNullOrBlank()) return false
        val validator = LanguageNamesValidation.INSTANCE.forLanguage(element.language)
        if (validator.isKeyword(selectedText, project)) return false
        if (validator.isIdentifier(selectedText, project)) return false

        return isStatementOrCodeBlock(element.containingFile, selectedRange)
    }

    override fun generatePreview(project: Project, editor: Editor, file: PsiFile): IntentionPreviewInfo = try { super.generatePreview(project, editor, file) } catch (e: PreviewException) { e.previewInfo }

    /**
     * when invokeForPreview, editor is copy as ImaginaryEditor,
     * and it selectionModel#selectText will be return null. It makes the preview out of available.
     */
    final override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val isPreviewEditor = isPreviewEditor(editor)
        val (code, range) = editor.applyIf(isPreviewEditor) {
            FileEditorManager.getInstance(project)
                ?.selectedEditor
                ?.let { it as TextEditor }
                ?.editor ?: return
        }.let {
            val range = TextRange.create(
                it.document.getLineStartOffset(it.document.getLineNumber(it.selectionModel.selectionStart)),
                it.selectionModel.selectionEnd
            )
            it.document.getText(range) to range
        }

        if (code.isBlank()) return
        if (code.length > AppSettingsState.instance.maxTokens) {
            if (isPreviewEditor) throw PreviewException(IntentionPreviewInfo.Html(WizardGptBundle.message("exceed.maxTokens")))
            WizardGptNotifier.notifyWarn(project, WizardGptBundle.message("exceed.maxTokens")); return
        }

        val prompt = wrapPrompt(code, element.language)
        kotlin.runCatching {
            invokeGpt(project, prompt)
        }.onFailure {
            if (isPreviewEditor) throw PreviewException()
            WizardGptNotifier.notifyError(project, it.localizedMessage)
        }.onSuccess { modifiedCode: String? ->
            if (modifiedCode.isNullOrBlank()) throw PreviewException()
            editor.document.replaceString(range.startOffset, range.endOffset, modifiedCode)
            // When generate preview, skipped the below:
            if (isPreviewEditor) return

            // erase current caret without selection
            editor.caretModel.currentCaret.removeSelection()
            // reformat the start ~ end(refactored) statement/code-block
            CodeStyleManager.getInstance(project).reformatText(
                element.containingFile,
                listOf(range.grown(modifiedCode.length))
            )
        }

    }

    private fun isStatementOrCodeBlock(file: PsiFile, range: TextRange): Boolean {
        var startOffset = range.startOffset
        var endOffset = range.endOffset
        val language = file.language
        var element1: PsiElement? = file.viewProvider.findElementAt(startOffset, language) ?: return false
        var element2: PsiElement? = file.viewProvider.findElementAt(endOffset - 1, language) ?: return false

        if (element1 is PsiWhiteSpace) {
            startOffset = element1.getTextRange().endOffset
            element1 = file.viewProvider.findElementAt(startOffset, language)
        }
        if (element1 == null || element1.startOffset != startOffset) return false

        if (element2 is PsiWhiteSpace) {
            endOffset = element2.getTextRange().startOffset
            element2 = file.viewProvider.findElementAt(endOffset - 1, language)
        }
        if (element2 == null || element2.endOffset != endOffset) return false

        // 非支持的语言不会抽离选中的文本块中的有效statement.
        val elementTypeMatcher = ElementTypeMatcher.INSTANCE[language] ?: return true

        var prev: PsiElement? = element1
        while (prev != null && prev.endOffset < endOffset) {
            prev = file.viewProvider.findElementAt(prev.endOffset, language)
        }

        if (prev != element2) return false
        val parent = PsiTreeUtil.findCommonParent(element1, element2) ?: return false
        return elementTypeMatcher.let {
            it.isStatement(element1, element2, parent) || it.isCodeBlock(element1, element2, parent)
        }
    }

    final override fun getIcon(flags: Int): Icon {
        return AllIcons.Actions.SuggestedRefactoringBulb
    }
}

class PreviewException(val previewInfo: IntentionPreviewInfo = IntentionPreviewInfo.EMPTY) : RuntimeException()