@file:Suppress("UnstableApiUsage")

package com.github.icoder.wizardgpt.statusBar

import com.github.icoder.wizardgpt.WizardGptBundle
import com.github.icoder.wizardgpt.WizardGptIcons
import com.github.icoder.wizardgpt.settings.AppSettingsConfigurable
import com.github.icoder.wizardgpt.settings.AppSettingsState
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.impl.status.IdeStatusBarImpl
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.ClickListener
import com.intellij.ui.GotItTooltip
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.listCellRenderer
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.update.Activatable
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JTextField

@Suppress("DialogTitleCapitalization")
class WizardGptWidgetPanel(val project: Project) : TextPanel.WithIconAndArrows(), CustomStatusBarWidget, Activatable {
    private var myStatusBar: StatusBar? = null

    private fun popupPanel() = panel {
        indent {
            row {
                icon(AllIcons.General.Settings).gap(RightGap.SMALL)
                label("OPTIONS").bold()
                button("Configure...", ::linkWizardGptConfigure).align(AlignX.RIGHT)

            }.topGap(TopGap.SMALL)
        }
        indent {
            row {
                label("Temperature")
                spinner(0.1..1.0, step = 0.1)
                    .align(AlignX.RIGHT)
                    .enabled(AppSettingsState.instance.apiEnabled)
                    .bindValue(AppSettingsState.instance::temperature)
                    .applyToComponent()
                    {
                        isOpaque = false
                        border = JBUI.Borders.empty()
                        addChangeListener { AppSettingsState.instance.temperature = this.value as Double }
                    }
            }
        }
        indent {
            row {
                label("MaxTokens")
                spinner(32..Int.MAX_VALUE, step = 32)
                    .align(AlignX.RIGHT)
                    .enabled(AppSettingsState.instance.apiEnabled)
                    .bindIntValue(AppSettingsState.instance::maxTokens)
                    .applyToComponent()
                    {
                        isOpaque = false
                        border = JBUI.Borders.empty()
                        addChangeListener { AppSettingsState.instance.maxTokens = this.value as Int }
                    }
            }
        }
        indent {
            row {
                label("Model")
                comboBox(AppSettingsState.instance.apiModels, listCellRenderer { value, _, _ -> setText(value) })
                    .align(AlignX.RIGHT)
                    .enabled(AppSettingsState.instance.apiEnabled)
                    .bindItem(AppSettingsState.instance::apiModel.toNullableProperty())
                    .applyToComponent()
                    {
                        isOpaque = false
                        border = JBUI.Borders.empty()
                        isEditable = true
                        with(editor.editorComponent as JTextField) { this.horizontalAlignment = JBTextField.RIGHT }
                        addItemListener { if (it.stateChange == ItemEvent.SELECTED) { AppSettingsState.instance.apiModel = it.item as String } }
                    }
            }
        }
    }

    init {
        border = JBUI.Borders.empty(0, 2)
        isFocusable = false
        object : ClickListener() {
            override fun onClick(e: MouseEvent, clickCount: Int): Boolean {
                showPopup(e)
                return true
            }
        }.installOn(this, true)

        updateState()
        updateUI()
    }

    private fun showPopup(e: MouseEvent) {
        val popupPanel = popupPanel()
        val popup =
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupPanel, popupPanel.preferredFocusedComponent)
                .setRequestFocus(true)
                .setFocusable(true)
                .setShowShadow(true)
                .createPopup()
        val dimension = popup.content.preferredSize
        val at = Point(0, -dimension.height)
        popup.show(RelativePoint(e.component, at))

        Disposer.register(this, popup) // destroy popup on unexpected project close
    }

    private fun linkWizardGptConfigure() = ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable::class.java)
    private fun linkWizardGptConfigure(@Suppress("UNUSED_PARAMETER") e: ActionEvent) = ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable::class.java)


    @Suppress("IncorrectParentDisposable")
    private fun showTooltips() {
        GotItTooltip(WizardGptBundle.message("name"), WizardGptBundle.message("tooltips"), project)
            .withHeader("Wizard GPT status")
            .withLink("Configure", this::linkWizardGptConfigure)
            .show(this, GotItTooltip.TOP_MIDDLE)
    }

    fun updateState() {
        setTextAlignment(CENTER_ALIGNMENT)
        border = JBUI.CurrentTheme.StatusBar.Widget.border()
        text = "Wizard GPT"
        icon = if (AppSettingsState.instance.apiEnabled) WizardGptIcons.on else WizardGptIcons.off
    }

    override fun ID(): String = WizardGptBundle.message("widget.id")

    override fun install(statusBar: StatusBar) {
        this.myStatusBar = statusBar
        if (statusBar is IdeStatusBarImpl) statusBar.border = BorderFactory.createEmptyBorder(1, 0, 0, 6)
        if (!AppSettingsState.instance.apiEnabled) showTooltips()
    }

    override fun dispose() {
        if (myStatusBar is IdeStatusBarImpl) (myStatusBar as IdeStatusBarImpl).border =
            BorderFactory.createEmptyBorder(1, 0, 0, 0)
        myStatusBar = null
    }

    override fun getComponent(): JComponent = this
}