#!/usr/bin/env bash

if  [ -z "$JAVA_HOME" ]
then
  JAVA_HOME="/usr/lib/jvm/java-7-openjdk-i386/"
fi

ant $*
