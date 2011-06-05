@echo off

# Directory for verveine source
set ROOT=%CD%
set BASELIB=%ROOT%\lib

# JVM options e.g. -Xmx2500m to augment maximum memory size of the vm to 2.5Go.
set JOPT="-Xmx2000m"
# Verveine option
VOPT="."

CLASSPATH=${CLASSPATH}:${BASELIB}\verveine.extractor.java.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\fame.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\akuhn-util-r28011.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\famix.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\verveine.core.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.jdt.core_3.6.0.v_A58.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.core.contenttype_3.4.100.v20100505-1235.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.core.jobs_3.5.0.v20100515.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.core.resources_3.6.0.v20100526-0737.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.core.runtime_3.6.0.v20100505.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.equinox.common_3.6.0.v20100503.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.equinox.preferences_3.3.0.v20100503.jar
CLASSPATH=${CLASSPATH}:${BASELIB}\org.eclipse.osgi_3.6.0.v20100517.jar


java %JOPT% -cp %CLASSPATH% fr.inria.verveine.extractor.java.VerveineJParser %VOPT%