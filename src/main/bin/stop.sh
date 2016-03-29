#!/bin/sh

var=cache-benchmark
pid=`ps -ef | grep ${var} | awk '{print $1, $2, $8}' | grep java | awk '{print $2}'`
if [ "${pid}" != "" ]
then
  kill -9  $pid
fi
echo stop $var