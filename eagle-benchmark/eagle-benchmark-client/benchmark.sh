#!/bin/bash

JAVA_OPTIONS="-server -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms1096m -Xmx1096m"

java -jar ./target/microbenchmarks.jar -jvmArgs "$JAVA_OPTIONS" -t 16 -f 2
