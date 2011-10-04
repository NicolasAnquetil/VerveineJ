@echo off
setlocal ENABLEDELAYEDEXPANSION

rem Directory for verveine source
set ROOT=%~dp0%
set BASELIB=%ROOT%lib

rem JVM options e.g. -Xmx2500m to augment maximum memory size of the vm to 2.5Go.
set JOPT="-Xmx2000m"
rem Verveine option
rem set VOPT="."

set LOCALCLASSPATH=%BASELIB%\verveine.extractor.java.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\fame.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\akuhn-util-r28011.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\famix.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\verveine.core.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.jdt.core_3.6.0.v_A58.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.core.contenttype_3.4.100.v20100505-1235.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.core.jobs_3.5.0.v20100515.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.core.resources_3.6.0.v20100526-0737.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.core.runtime_3.6.0.v20100505.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.equinox.common_3.6.0.v20100503.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.equinox.preferences_3.3.0.v20100503.jar
set LOCALCLASSPATH=%LOCALCLASSPATH%;%BASELIB%\org.eclipse.osgi_3.6.0.v20100517.jar;
rem FOR /R . %%G IN (*.jar) DO set LOCALCLASSPATH=!LOCALCLASSPATH!;%%G
set CLASSPATH=%LOCALCLASSPATH%

java %JOPT% fr.inria.verveine.extractor.java.VerveineJParser %1 %2 %3 %4 %5 %6 %7 %8 %9