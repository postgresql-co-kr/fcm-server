#!/bin/sh
kill -15 `cat application.pid`
echo "Fcm server stopped"
