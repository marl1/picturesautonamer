#!/bin/bash

# Unset the JLINK_VM_OPTIONS variable
unset JLINK_VM_OPTIONS

# Set the DIR variable to the script's directory
DIR="$(dirname "$0")"

# Start javaw with the specified options and classpath
javaw $JLINK_VM_OPTIONS -m fr.pan/fr.pan.main.Main "$@"