rm image.tar.bz2
echo "[i] saving and compressing image"
docker save csp-jitsimeet:1.0 | bzip2 -9 - > image.tar.bz2
echo "[i] done, retcode $?"
