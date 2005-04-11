#!/bin/sh

$JAVA_HOME/bin/java -cp \
bin:\
${HOME}/.maven/repository/commons-httpclient/jars/commons-httpclient-2.0.jar:\
${HOME}/.maven/repository/commons-logging/jars/commons-logging-1.0.4.jar \
\
net.cyklotron.tools.ComparisonRobot \
\
src/main/robot.conf \
$@
