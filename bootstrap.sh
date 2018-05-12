#!/usr/bin/env bash
# This file is ran during vagrant provision
add-apt-repository ppa:webupd8team/java
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2930ADAE8CAF5059EE73BB4B58712A2291FA4AD5
echo "deb [ arch=amd64 ] https://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.6 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.6.list
apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
apt-get install -y git maven oracle-java8-installer mongodb-org-server
cd /vagrant/
mvn clean
mvn install
cd target
java -jar server-1.4a.jar