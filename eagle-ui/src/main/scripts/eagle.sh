#!/usr/bin/env bash

BASEDIR=$(cd `dirname $0`; pwd)

EAGLE_HOME=$BASEDIR/..

LIB_HOME=$EAGLE_HOME/lib

CONF_FILE=$EAGLE_HOME/conf/eagle.conf
. $CONF_FILE

JAR_FILE=$LIB_HOME/eagle-ui-1.0.jar

PID_FILE=$EAGLE_HOME/eagle.pid

# JAVA_OPTS
JAVA_OPTS="-server -Duser.dir=$BASEDIR -Deagle.logPath=$LOG_PATH"
JAVA_OPTS="${JAVA_OPTS} $JAVA_HEAP_OPTS"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:HeapDumpPath=$LOG_PATH -Xloggc:$LOG_PATH/gc.log"

# CONFIG_OPTS
CONFIG_OPTS="--server.address=$BIND_ADDR --server.port=$LISTEN_PORT"
CONFIG_OPTS="$CONFIG_OPTS --eagle.user=$EAGLE_USER --eagle.pass=$EAGLE_PASS"

function start()
{
    java $JAVA_OPTS -jar $JAR_FILE $CONFIG_OPTS $1 > /dev/null 2>&1 &
    echo $! > $PID_FILE
}

function stop()
{
    pid=`cat $PID_FILE`
    echo "kill $pid ..."
    kill $pid
    rm -f $PID_FILE
}

args=($@)

case "$1" in

    'start')
        start
        ;;

    'stop')
        stop
        ;;

    'restart')
        stop
        start
        ;;

    'help')
        help $2
        ;;
    *)
        echo "Usage: $0 { start | stop | restart | help }"
        exit 1
        ;;
esac