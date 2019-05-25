#!/bin/bash

TOP=$(realpath $(pwd)/$(dirname $BASH_SOURCE)/..)

WEBAPP_OUT=$TOP/webapp/out
APP_NAME=moo

WEBAPP_WAR=$WEBAPP_OUT/war
rm -rf $WEBAPP_WAR
mkdir -p $WEBAPP_WAR/WEB-INF/classes
cp -r $WEBAPP_OUT/moo $WEBAPP_WAR/WEB-INF/classes
cp $WEBAPP_OUT/web.xml $WEBAPP_WAR/WEB-INF
jar cf $WEBAPP_OUT/$APP_NAME.war -C $WEBAPP_WAR .
rm -rf $WEBAPP_WAR

cp $WEBAPP_OUT/$APP_NAME.war -t /var/lib/tomcat8/webapps
