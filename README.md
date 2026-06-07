# jetbrains-amber-plugin

<!-- Plugin description -->
**JetBrains Amber Language Plugin** is a plugin to support [Amber Language](https://amber-lang.com/) in JetBrains IDEs.

Amber is a modern, type-safe programming language that can be compiled to Bash/Ksh/Zsh.

Amber features:

- Modern Syntax: ECMA Script-like syntax
- Runtime Safety: Amber ensures that you handle everything that could fail
- Type Safety: use strong typing to catch bugs at compile time
- Instant Docs: generate documentation automatically
- Shell Ready: Interoperate with Bash/Ksh/Zsh scripts
- Library baked in: Standard library with many useful functions

Amber JetBrains plugin features:

* Amber language and file types
* Syntax highlight
* Amber file structure
* Run a script from shebang or main function
* Run a test case from the test gutter icon
* Run configuration support: `Run -> Edit Configurations -> Amber` or right-click in an Amber script
* Code completion: std library, imported members, function, keywords, etc.
* Navigation support for function
* Basic support for formatting
* live templates for import and function call
* External annotator to use `amber check` to collect errors and warnings
* Inspection: auto-import for unresolved function from std library

How to get started?

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

Enjoy shell scripting with Amber!

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "amber"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/32151-amber-language) and install it by clicking
  the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/32151-amber-language/versions) from
  JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/linux-china/jetbrains-amber-plugin/releases/latest) and install it
  manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---

