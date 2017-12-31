# [Live demo](https://muchassemblyrequired.com)
Program the 8086-like microprocessor of a robot in a grid-based multiplayer world. The game is web based so no installation is required.
In its current state, players can walk around the game universe and collect Biomass blobs & Iron/copper ore using the online code editor.

![screenshot from 2017-11-12 13-01-43](https://user-images.githubusercontent.com/7120851/32701793-e5d07e98-c7a9-11e7-9931-f8db7b287994.png)

Wiki: [GitHub](https://github.com/simon987/Much-Assembly-Required/wiki)    
Chat: [Slack](https://join.slack.com/t/muchassemblyrequired/shared_invite/enQtMjY3Mjc1OTUwNjEwLTkyOTIwOTA5OGY4MDVlMGI4NzM5YzlhMWJiMGY1OWE2NjUxODQ1NWQ1YTcxMTA1NGZkYzNjYzMyM2E1ODdmNzg)

# Deploying the server (Ubuntu or other Debian derivative)

Note: You can find the frontend [here](https://github.com/simon987/Much-Assembly-Required)


## Linux (Ubuntu 16.04)
```bash
# Install tools
sudo apt install git maven openjdk-8-jdk

# Obtain source files
git clone https://github.com/simon987/Much-Assembly-Required.git

# Build
cd Much-Assembly-Required
mvn package

# Run
cd target
java -jar server-1.2a.jar
```

## Windows
Coming eventually...
