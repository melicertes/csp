echo "Building all docker files"
echo ""
CWD=`pwd`

echo "building Alpine base image"
cd ./base-images/alpine35base && ./build.sh ; cd $CWD
echo "building Java 8 base image"
cd ./base-images/java8 && ./build.sh ; cd $CWD
echo "building Apache base image"
cd ./base-images/apache2/ && ./build.sh ; cd $CWD
echo "building Postgres base image"
cd ./base-images/postgres/ && ./build.sh ; cd $CWD
echo "building Python2 base image"
cd ./base-images/python27/ && ./build.sh ; cd $CWD
echo "building Python2v base image"
cd ./base-images/python27-viper/ && ./build.sh ; cd $CWD
echo "building Tomcat 8 base image"
cd ./base-images/tomcat8 && ./build.sh ; cd $CWD
echo "building dockerjava8 image"
cd ./base-images/dockerjava8/ && ./build.sh ; cd $CWD
echo "building misp base image"
cd ./base-images/misp-image/ && ./build.sh ; cd $CWD

echo "build complete; report: "
echo ""
echo ""
echo "Image"
echo "--------------"
echo "`docker images |grep csp- | awk '{ print $1":"$2 }'`"

