#!/bin/bash
# Create a new distribution of VerveineJ for an existing licence
# Needs licence key as parameter


TMPDIR="tmp.dir"
ANTTARGET="jar"


if [ "$#" != "1" ]
then
    echo "usage: $0 <licence-key>"
    exit 2
else
    KEY="$1"
fi

#rebuild everything too make sure we are clean
ant -f ../build.xml "${ANTTARGET}"

# copy all needed files in a temp dir
mkdir "${TMPDIR}"
cd "${TMPDIR}"
mkdir lib
cp ../../lib/* lib
cp ../../verveinej.sh .
cp ../../verveinej.bat .

# register key
echo -n $KEY >> lib/famix.jar

#make zip file
zip verveinej.zip lib/* verveinej.sh verveinej.bat
mv verveinej.zip ..

# remove temp stuff
cd ..
rm -rf  "${TMPDIR}"
