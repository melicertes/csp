rm image.tar.bz2

docker save csp-jitsiopenfire:1.0p2 csp-jitsivcbridge:1.0p2 > image.tar
rm image.tar.bz2
bzip2 *tar
