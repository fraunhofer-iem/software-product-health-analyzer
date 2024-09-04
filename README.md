# Software Product Health Analyzer (SPHA)

## Code Style

We use [ktfmt](https://github.com/facebook/ktfmt?tab=readme-ov-file) as a code style checker and reformater with the
`kotlinlang` code style.

The [gradle plugin](https://github.com/cortinico/ktfmt-gradle#how-to-use-) is installed for all submodules through the
conventions plugin. It adds two gradle tasks
`./gradlew ktfmtCheck` and `./gradlew ktfmtFormat`. The gradle plugin is configured to use `kotlinlang`.
ktfmt has an [IntelliJ plugin](https://plugins.jetbrains.com/plugin/14912-ktfmt) which automatically replaces IntelliJ's
format command and applies ktfmt's formatting. The IntelliJ plugin must be configured to use `kotlinlang` under the
plugin's settings. 