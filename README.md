### [Official website](https://muchassemblyrequired.com)
Program the 8086-like microprocessor of a robot in a grid-based multiplayer world. The game is web based so no installation is required.
In its current state, players can walk around the game universe and collect Biomass blobs & Iron/copper ore using the online code editor.

![screenshot from 2017-11-12 13-01-43](https://user-images.githubusercontent.com/7120851/32701793-e5d07e98-c7a9-11e7-9931-f8db7b287994.png)

Wiki: [GitHub](https://github.com/simon987/Much-Assembly-Required/wiki)
Chat: [Slack](https://join.slack.com/t/muchassemblyrequired/shared_invite/enQtMjY3Mjc1OTUwNjEwLTkyOTIwOTA5OGY4MDVlMGI4NzM5YzlhMWJiMGY1OWE2NjUxODQ1NWQ1YTcxMTA1NGZkYzNjYzMyM2E1ODdmNzg)

# Deploying the server

## Linux

**Installing tools**

On Ubuntu 16.04:
```bash
sudo apt install git maven openjdk-8-jdk mongodb
```

On Arch:
``` bash
sudo pacman -S git maven mongodb jdk8-opendjk

# Don't forget to start mongodb
sudo systemctl start mongodb.service
```

*If needed, visit [troubleshooting mongodb](https://wiki.archlinux.org/index.php/MongoDB#Troubleshooting).*

**Deploying server**

``` bash
# Obtain source files
git clone https://github.com/simon987/Much-Assembly-Required.git

# Build
cd Much-Assembly-Required
mvn package

# Run
cd target
java -jar server-1.4a.jar
```

## Windows (tested on Windows 10)

Installation instructions:
1. Download the JDK from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
Install the JDK and update your PATH and JAVA_HOME enviroment variables.
2. Download Maven from [here](https://maven.apache.org/).
Install Maven (following the README) and update your PATH enviroment variable.
3. Download Mongo DB Community from [here](https://www.mongodb.com/download-center#community).
Install Mongo DB following the instructions [here](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/).
Update your PATH enviroment variable.

Building instructions:
```batch
:: Builds the server
cd Much-Assembly-Required
mvn package
```

Running instructions:
1. In one Command Prompt window, run Mongo DB:
```batch
:: Runs Mongo DB
mongod
```
2. In a second Command Prompt window, run the MAR server:
```batch
:: Runs the MAR server
cd Much-Assembly-Required\target
java -jar server-1.4a.jar
```

## macOS (tested on Sierra 10.12.6)

**Installation**

1. Install [Maven3](https://maven.apache.org/)
   -Add Maven bin to your path
   ```bash
   export PATH=/path/to/maven/bin.:$PATH
   ```
2. Install [MongoDB](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/?_ga=2.201359831.774868398.1539369140-197602459.1539369140).
   -Via Brew:
   ```bash
   #Update brew
   brew update

   #Install mongodb
   brew install mongodb

   #Install latest development release
   brew install mongodb --devel
   ```
   -Via .tar.gz
   ```bash
   #Extract files:
   tar -zxvf mongodb-osx-ssl-x86_64-4.0.3.tgz
   
   #Ensure binaries are in your path
   export PATH=<mongodb-install-directory>/bin:$PATH
   ```
If you do not wish to use the default data directory (/data/db), follow the steps for running MongoDB in the install doc.

**Deploying Server**

1. Begin MongoDB service
   ```bash
   #If brew:
   #Launch on login
   brew services start mongodb
   #Or, if you don't want/need a background service you can just run:
   mongod --config /usr/local/etc/mongod.conf
   
   #If binary:
   mongod
   #Optional, set data directory path:
   mongod --dbpath <path to data directory>
   ```

2. Deploy server:
   ```bash
   # Obtain source files
   git clone https://github.com/simon987/Much-Assembly-Required.git

   # Build
   cd Much-Assembly-Required
   mvn package

   # Run
   cd target
   java -jar server-1.4a.jar
   ```

## Docker
### Requirements

1. [Docker Compose](https://docs.docker.com/compose/install/#install-compose) (and dependencies)

### Installation

Once Docker and Docker Compose are installed, you can build and start
this application by running the following command inside this
application's directory:

`docker-compose up`

Make sure to change `mongo_address` in `config.properties` to `mongodb`.


## Vagrant
### Requirements
1. [Vagrant](https://www.vagrantup.com/downloads.html)
2. [VirtualBox](https://www.virtualbox.org/wiki/Downloads)

### Installation

When vagrant is installed, you can build and start this application by running the following
command inside this application's directory:

`vagrant up`

# Running

Once the server is running, you should be able to connect to `http://localhost:4567` with your browser

## VS Code Extensions
- [Much Assembly Required (Upload on Save)](https://marketplace.visualstudio.com/items?itemName=tomhodder.much-assembly-required-upload-on-save) by tomhodder
- [Much Assembly Required Language Support](https://marketplace.visualstudio.com/items?itemName=PJB3005.much-assembly-required-language-support) by PJB3005
