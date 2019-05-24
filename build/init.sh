#!/bin/bash

TOP=$(realpath $(pwd)/$(dirname $BASH_SOURCE)/..)

ln -s ../lib -t $TOP/diarystorage

