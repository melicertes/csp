echo "Building all docker files"
echo ""
CWD=`pwd`

echo "building Alpine 3.5 base image"
cd ./base-images/alpine35base && ./build.sh ; cd $CWD
echo "building Java 8 base image"
cd ./base-images/java8 && ./build.sh ; cd $CWD
echo "building Apache base image"
cd ./base-images/apache2/ && ./build.sh ; cd $CWD
echo "building Postgres base image"
cd ./base-images/postgres/ && ./build.sh ; cd $CWD
echo "building Python3 base image"
cd ./base-images/python3/ && ./build.sh ; cd $CWD
echo "building Python2 base image"
cd ./base-images/python27/ && ./build.sh ; cd $CWD
echo "building Tomcat 7.0.77 base image"
cd ./base-images/tomcat7 && ./build.sh ; cd $CWD
echo "building Tomcat 8.5.13 base image"
cd ./base-images/tomcat8 && ./build.sh ; cd $CWD
# echo "building ElasticSearch base image"
# cd ./base-images/elasticsearch && ./build.sh ; cd $CWD

if [ "$1xx" == "basexx" ]; 
then
	exit 0
fi

echo "building Jitsi"
cd ./applications/jitsi && ./build.sh ; cd $CWD
echo "building OpenAm"
cd ./applications/openam && ./build.sh ; cd $CWD
echo "building IntelMQ"
cd ./applications/intelmq && ./build.sh ; cd $CWD
echo "building OwnCloud"
cd ./applications/owncloud  && ./build.sh ; cd $CWD

echo "build complete; report: "
echo ""
echo ""
echo "Image"
echo "--------------"
echo "`docker images |grep csp- | awk '{ print $1":"$2 }'`"

