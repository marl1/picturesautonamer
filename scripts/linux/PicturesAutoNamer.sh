#!/bin/sh
JLINK_VM_OPTIONS=
DIR=`dirname $0`
$DIR/bin/java $JLINK_VM_OPTIONS -m fr.pan/fr.pan.main.Main "$@"