package com.github.icoder.wizardgpt.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

object WizardGptNotifier {
    fun notifyError(project: Project?, content: String) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("WizardGPT")
            .createNotification("Wizard GPT", content, NotificationType.ERROR)
        notification.addAction(object : AnAction("Dismiss") {
            override fun actionPerformed(e: AnActionEvent) {
                notification.expire()
            }
        })
        Notifications.Bus.notify(notification, project)
    }

    fun notifyWarn(project: Project?, content: String) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("WizardGPT")
            .createNotification("Wizard GPT", content, NotificationType.WARNING)
        notification.addAction(object : AnAction("Dismiss") {
            override fun actionPerformed(e: AnActionEvent) {
                notification.expire()
            }
        })
        Notifications.Bus.notify(notification, project)
    }
}