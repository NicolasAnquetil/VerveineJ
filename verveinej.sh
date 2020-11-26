#!/usr/bin/env bash
((ddashloc=0))
((i=0))
for f in "$@"; do
  ((i++))
  #echo "$f";
  if [ "$f" == "--" ]
  then
    #echo " found the -- at $i"
    ((ddashloc= $i+1))
  fi
done

# Directory for verveine source
BASELIB=`dirname "$0"`/lib

# on windows, convert backslashes to slashes so that file expansion will work
BASELIB=$(sed 's:\\:/:g' <<< "$BASELIB")

pathsep=":"
# handle path separator for flavors of windows (MINGW in LibC via Pharo)
case $(uname) in
MINGW*|CYGWIN*)
  pathsep=";"
esac
for i in "$BASELIB"/*.jar; do
    CLASSPATH="$CLASSPATH"$pathsep"$i"
done
CLASSPATH=`echo $CLASSPATH | cut -c2-`

if [ $ddashloc != 0 ]
then
  java ${@:1:(($ddashloc-2))} -cp "$CLASSPATH" fr.inria.verveine.extractor.java.VerveineJMain "${@:$ddashloc}" #-o "$msefile" "$sourcepath"
else
  java -cp "$CLASSPATH" fr.inria.verveine.extractor.java.VerveineJMain "${@:1}" #-o "$msefile" "$sourcepath"
fi
