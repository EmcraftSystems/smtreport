#!/bin/bash

POM_FILE="`dirname $0`/pom.xml"

mvn -q -f $POM_FILE exec:java -Dexec.mainClass="com.emcraft.smtreport.SMTReportProcessor" -Dexec.args="$*"
