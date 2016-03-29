#! /bin/bash

bin='dirname "$0"'
JAVA_HOME=/opt/jdk1.7.0_67
# some Java parameters
if [ "$JAVA_HOME" != "" ]; then
  #echo "run java in $JAVA_HOME"
  JAVA_HOME=$JAVA_HOME
fi
  
if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

JAVA=$JAVA_HOME/bin/java

# CLASSPATH initially

for f in ../lib/*.jar; do
   CLASSPATH=${CLASSPATH}:$f;
done

for f in ./*.jar; do
   CLASSPATH=${CLASSPATH}:$f;
done
 
 
CLASSPATH=${CLASSPATH}:../conf; 


# get arguments
CLASS=com.ctg.itrdc.cache.performance.CountResult

echo $CLASSPATH
exec "$JAVA" -classpath "$CLASSPATH" $CLASS "$@"
