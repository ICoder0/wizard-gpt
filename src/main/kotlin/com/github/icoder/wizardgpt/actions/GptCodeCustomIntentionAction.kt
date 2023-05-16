package com.github.icoder.wizardgpt.actions

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonShortcuts
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.util.PopupUtil
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.registry.Registry
import com.intellij.psi.PsiElement
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollBar
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import java.awt.Font
import javax.swing.ScrollPaneConstants
import javax.swing.SwingConstants

class GptCodeCustomIntentionAction : GptCodeBrushIntentionAction() {
    override fun getText() = "Brush CUSTOM"
    override fun getFamilyName() = text

    private fun wrapPrompt(code: String, language: Language, prompt: String): String = buildString {
        append("Act as ${language.id} Programmer")
        append("\n")
        append(prompt)
        append("\n")
        append("### Origin")
        append("\n")
        append(code)
        append("\n")
        append("### Modified")
    }

    override fun brush(project: Project, editor: Editor, element: PsiElement, code: String, range: TextRange, prompt: String) {
        val editorTextField = EditorTextField("", project, PlainTextFileType.INSTANCE)
        editorTextField.border = PopupUtil.createComplexPopupTextFieldBorder()
        editorTextField.isFocusable = true
        editorTextField.isOpaque = false
        if (Registry.`is`("new.search.everywhere.use.editor.font")) {
            editorTextField.font = EditorUtil.getEditorFont()
        }

        val fontDelta = Registry.intValue("new.search.everywhere.font.size.delta")
        if (fontDelta != 0) {
            var font: Font = editorTextField.font
            font = font.deriveFont(fontDelta.toFloat() + font.size)
            editorTextField.font = font
        }

        editorTextField.addSettingsProvider { _editor: EditorEx ->
            val scrollPane = _editor.scrollPane
            scrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            val verticalScrollBar = scrollPane.verticalScrollBar
            verticalScrollBar.background = _editor.backgroundColor
            verticalScrollBar.add(JBScrollBar.LEADING, JBLabel("Type customized prompt", AllIcons.Actions.Execute, SwingConstants.LEADING))
            verticalScrollBar.isOpaque = true
        }

        editorTextField.setPreferredWidth(JBUI.scale(400))
        editorTextField.setOneLineMode(true)

        val balloon = JBPopupFactory.getInstance().createBalloonBuilder(editorTextField)
            .setShadow(true)
            .setAnimationCycle(0)
            .setRequestFocus(true)
            .setHideOnClickOutside(false)
            .setHideOnKeyOutside(true)
            .setHideOnAction(false)
            .setBorderInsets(JBInsets.create(0, 0))
            .createBalloon()
        DumbAwareAction.create { _: AnActionEvent? -> balloon.hide() }
            .registerCustomShortcutSet(CommonShortcuts.ESCAPE, editorTextField)
        DumbAwareAction.create { _: AnActionEvent? ->
            super.brush(project, editor, element, code, range, wrapPrompt(code, element.language, editorTextField.text))
            balloon.hide()
        }.registerCustomShortcutSet(CommonShortcuts.ENTER, editorTextField)

        @Suppress("IncorrectParentDisposable")
        Disposer.register(element.project, balloon)
        balloon.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.above)
    }
}