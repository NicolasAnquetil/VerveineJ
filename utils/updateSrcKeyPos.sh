#!/bin/bash
# Sets the position of the licence number in the licence checker
# Called from ant


BASEDIR=`dirname $0`/..
LICFILE="eu/synectique/licence/LicenceChecker.java"
JARFILE="${BASEDIR}/lib/famix-20160614_1015.jar"
TMPFILE="tmp.$$"

KEYPOS=`wc -c < "${JARFILE}"`

sed -e 's/KEY_POS = [0-9]*;/KEY_POS = '"${KEYPOS}"';/' "${LICFILE}" > "${TMPFILE}"

mv "${TMPFILE}" "${LICFILE}"
