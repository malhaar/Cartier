#!/usr/bin/env bash
cd ..
basepath=$(cd `dirname $0`; pwd)
export AAT_HOME="$basepath"
java -jar macOS/apple-account-tools.jar $* $JAVA_OPTS