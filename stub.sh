#!/bin/sh

MYSELF="$(readlink -f "$0")"

JAVA_BIN=java
[ -n "$JAVA_HOME" ] && JAVA_BIN="$JAVA_HOME/bin/java"

exec "$JAVA_BIN" \
  -jar "$MYSELF" "$@"
  # -XX:+PrintFlagsFinal \
