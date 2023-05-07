package com.github.icoder.wizardgpt

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.AnimatedIcon

object WizardGptIcons {
    @JvmField
    val offline = IconLoader.getIcon("/icons/offline.svg", javaClass)

    @JvmField
    val offline2 = IconLoader.getIcon("/icons/offline2.svg", javaClass)
    val off = AnimatedIcon(
        100,
        offline, offline, offline, offline, offline,
        offline, offline, offline, offline, offline,
        offline, offline, offline, offline, offline,
        offline, offline, offline, offline, offline,
        offline, offline, offline, offline, offline,
        offline2, offline2,
    )

    @JvmField
    val online = IconLoader.getIcon("/icons/online.svg", javaClass)
    val online2 = IconLoader.getIcon("/icons/online2.svg", javaClass)
    val online3 = IconLoader.getIcon("/icons/online3.svg", javaClass)
    val online4 = IconLoader.getIcon("/icons/online4.svg", javaClass)
    val on = AnimatedIcon(
        100,
        online, online, online, online, online,
        online, online, online, online, online,
        online, online, online, online, online,
        online, online, online, online, online,
        online, online, online, online, online,
        online, online, online, online, online,
        online2, online3,
        online4, online4,
        online3, online3,
        online3, online2
    )
}