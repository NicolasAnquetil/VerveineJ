#!/usr/bin/env bash

if [ -z "$1" ]
then
	echo "Usage: verveinej.sh [ <jvm-options> -- ] [ <verveine-options> ] <java-source>" >&2
	echo "  <verveine-options> from Eclipse JDT Batch Compiler:" >&2
	echo "" >&2
fi

# Directory for verveine source
BASELIB=`dirname $0`/lib


# JVM options e.g. -Xmx2500m to augment maximum memory size of the vm to 2.5Go.
JOPT=""
# Verveine option
VOPT=""

# Any argument before "--" is for the JVM
# Any argument after "--" is for verveine
# Without "--" every argument goes to verveine
while [ "$1" != "--" ] && [ "$1" != "" ]
do
	JOPT="$JOPT "$(printf %q "$1")
	shift
done

if [ "$1" == "--" ]
then
	shift
	VOPT=$*
else
	# without the special "--" argument, all options are assumed to be for Verveine
	VOPT=$JOPT
	JOPT=""
fi

for i in $BASELIB/*.jar; do
    CLASSPATH=$CLASSPATH:$i
done
CLASSPATH=`echo $CLASSPATH | cut -c2-`

#echo $CLASSPATH

java $JOPT -cp $CLASSPATH eu.synectique.verveine.extractor.java.VerveineJParser $VOPT
