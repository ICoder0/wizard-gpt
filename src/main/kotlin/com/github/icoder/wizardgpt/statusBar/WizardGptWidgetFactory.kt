package com.github.icoder.wizardgpt.statusBar

import com.github.icoder.wizardgpt.WizardGptBundle
import com.github.icoder.wizardgpt.util.WizardGptTopics
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory

class WizardGptWidgetFactory : StatusBarEditorBasedWidgetFactory() {

    override fun getId(): String = WizardGptBundle.message("widget.id")

    override fun getDisplayName(): String = id

    override fun createWidget(project: Project): StatusBarWidget = WizardGptWidgetPanel(project).also {
        WizardGptTopics.subscribe(it, WizardGptWidgetPanel::updateState)
    }

    override fun disposeWidget(widget: StatusBarWidget) = Disposer.dispose(widget)

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true

    override fun isAvailable(project: Project): Boolean = true

    override fun isEnabledByDefault(): Boolean = true
}