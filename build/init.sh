#!/bin/bash

TOP=$(realpath $(pwd)/$(dirname $BASH_SOURCE)/..)

ln -s ../lib -t $TOP/diarystorage

ln -s ../lib -t $TOP/webapp
ln -s /usr/share/tomcat8/lib/servlet-api.jar -t $TOP/webapp/lib
