#!/bin/bash

function build() {
    # TODO compile java into $OUT/classes
    #rm -rf $OUT/classes

    pack
}

function pack() {
    rm -rf $OUT/war
    rm -rf $OUT/$APP_NAME.war

    mkdir -p $OUT/war/WEB-INF

    cp -r $OUT/classes $OUT/war/WEB-INF
    cp -rL $TOP/lib $OUT/war/WEB-INF

    cp -r $TOP/webpage/* $OUT/war
    cp -r $TOP/webroot/* $OUT/war

    jar cf $OUT/$APP_NAME.war -C $OUT/war .
}

function deploy() {
    cp $TOP/out/$APP_NAME.war -t /var/lib/tomcat8/webapps
}

TOP=$(realpath $(pwd)/$(dirname $BASH_SOURCE)/..)
OUT=$TOP/out
APP_NAME=moo

build
deploy
