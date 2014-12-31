#!/bin/bash
#Create a new distribution of VerveineJ for
# - new user passed as parameter $1
# - number of months for licence passed as parameter $2



SERVER="http://37.139.2.203/validator"
TMPDIR="tmp.dir"

if [ "$#" != "2" ]
then
    echo "usage: $0 <username> <nb-months>"
    exit 2
else
    USER="$1"
    MONTHS="$2"
fi

#rebuild everything too make sure we are clean
ant -f ../build.xml build

# create user get new key
echo ""
echo "Creating user: ${USER}"
NEWKEY=`curl "${SERVER}/add:${USER}:${MONTHS}"`  2> curl.log

echo "New key will be: ${NEWKEY}"
echo ""

# copy all needed files in a temp dir
mkdir "${TMPDIR}"
cd "${TMPDIR}"
mkdir lib
cp ../../lib/* lib
cp ../../verveinej.sh .
cp ../../verveinej.bat .

# register key
echo -n $NEWKEY >> lib/famix.jar

#make zip file
zip verveinej.zip lib/* verveinej.sh verveinej.bat
mv verveinej.zip ..

# remove temp stuff
cd ..
rm -rf  "${TMPDIR}"
