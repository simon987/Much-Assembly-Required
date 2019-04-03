#!/usr/bin/env bash

export MARROOT="mar"

screen -S mar -X quit
echo "Starting MAR"

cp ${MARROOT}/marConfig.properties ${MARROOT}/target/config.properties
cp -r ${MARROOT}/marCerts/ ${MARROOT}/target/certificates

screen -S mar -d -m bash -c "cd ${MARROOT}/target && java -jar server-*.jar"
sleep 1
screen -list