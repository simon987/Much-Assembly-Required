## General guide

[Collaboration guide](https://github.com/simon987/Much-Assembly-Required/wiki/Collaboration-Guide)

## Before creating a pull request

Here small unordered list of guidelines to read before creating a pull request
- Use java <= 1.8 features
- Please follow [Google's Java style guide](https://google.github.io/styleguide/javaguide.html)
- Constants (e.g. the energy cost of an in-game action) should be loaded from config.properties
- Use `Logmanager.LOGGER` to log messages to the console. Use `.fine()` for debugging messages and `.info()` for 
info for more important messages 
that are not too frequently used.
- Please state what tests have been performed in the pull request
