# wizard-gpt

![Build](https://github.com/BOFA1ex/wizard-gpt/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Verify the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [x] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the Plugin ID in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->

> The Wizard GPT Plugin is a powerful tool for developers who want to improve their coding efficiency and code quality. This plugin uses OpenAI's GPT-3.5 architecture to provide developers with various features that help make their code more readable, robust, and maintainable. Additionally, it provides an intention generation feature to create test cases to ensure code correctness.

## Features 
- Code Optimization: GPT Plugin helps optimize your code by suggesting changes in code structure, flow control, and algorithmic design. This feature ensures that the code is well-structured, easier to read, and has better performance.
- Code Refactoring: With GPT Plugin, developers can quickly identify areas of code that can be refactored, making the code easier to maintain, reducing technical debt, and improving the overall quality of the codebase.
- Intention Generation: The plugin provides the intention generation feature to assist in creating test cases. Developers can specify the expected behavior, and the plugin will generate a test case that validates that behavior, saving time and effort on manual testing.
- Error Detection: The plugin can detect common coding errors and suggest fixes. This feature helps to ensure the robustness of the codebase, making it less prone to errors and bugs.
- Documentation Generation: GPT Plugin can generate documentation for your codebase, making it easier for other developers to understand and use your code.

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "wizard-gpt"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/BOFA1ex/wizard-gpt/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
