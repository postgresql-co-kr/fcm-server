#!/bin/sh
JAVA_HOME=/usr/java17
export JAVA_HOME

nohup $JAVA_HOME/bin/java -jar -Xms512m -Xmx1024m fcm-server-0.0.1.jar --spring.profiles.active=dev --logging.config=./config/logback-spring.xml > /dev/null 2 >&1 &
echo "Fcm server started!"
