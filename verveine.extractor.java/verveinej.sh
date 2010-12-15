#!/bin/bash

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
while [ ${1:-"--"} != "--" ]
do
	JOPT="$JOPT $1"
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

CLASSPATH="${CLASSPATH}:${BASELIB}/verveine.extractor.java.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/fame.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/akuhn-util-r28011.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/famix.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/verveine.core.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.jdt.core_3.6.0.v_A58.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.core.contenttype_3.4.100.v20100505-1235.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.core.jobs_3.5.0.v20100515.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.core.resources_3.6.0.v20100526-0737.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.core.runtime_3.6.0.v20100505.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.equinox.common_3.6.0.v20100503.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.equinox.preferences_3.3.0.v20100503.jar"
CLASSPATH="${CLASSPATH}:${BASELIB}/org.eclipse.osgi_3.6.0.v20100517.jar"


java $JOPT -cp $CLASSPATH fr.inria.verveine.extractor.java.VerveineJParser $VOPT
