# jetbrains-amber-plugin

![Build](https://github.com/linux-china/jetbrains-amber-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)


<!-- Plugin description -->
**JetBrains Amber Language Plugin** is a plugin to support [Amber Language](https://amber-lang.com/) in JetBrains IDEs.

Plugin features:

* Amber language and file types
* Syntax highlight
* Run a script with shebang or main function
* Run a test case from the test gutter icon
* Run configuration support: `Run -> Edit Configurations -> Amber` or right-click in an Amber script 
* Code completion: std library, function, keywords, etc.
* Basic support for formatting

How to use?

* Download and install `amber` command line from https://docs.amber-lang.com/getting_started/installation
* Install JetBrains Amber plugin
* Create a simple Amber script with the following code:

```
#!/usr/bin/env amber

main {
 echo("Hello world")
}
```

* Run amber script from the shebang gutter run icon

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "amber"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking
  the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from
  JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/linux-china/jetbrains-amber-plugin/releases/latest) and install it
  manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---

