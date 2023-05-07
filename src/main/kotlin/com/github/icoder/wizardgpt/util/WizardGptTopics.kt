package com.github.icoder.wizardgpt.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.AppLevel

interface WizardGptTopics {
    companion object {
        @AppLevel
        var GPT_ENABLED_CHANGES = Topic(
            WizardGptManagerListener::class.java, Topic.BroadcastDirection.TO_DIRECT_CHILDREN
        )

        fun <L : Disposable> subscribe(disposable: L, action: L.() -> Unit) {
            val connection = ApplicationManager.getApplication().messageBus.connect(disposable)
            connection.subscribe<WizardGptManagerListener>(GPT_ENABLED_CHANGES, object : WizardGptManagerListener {
                override fun gptTokenChanged(): Unit = disposable.action()
            })
        }

        fun publish() {
            ApplicationManager.getApplication().messageBus
                .syncPublisher(GPT_ENABLED_CHANGES)
                .gptTokenChanged()
        }
    }

}

@FunctionalInterface
interface WizardGptManagerListener {
    fun gptTokenChanged()
}