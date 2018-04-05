#!/usr/bin/env bash
if [ -f image.tar.bz2 ]; then
  echo "[i] removing old data: image.tar.bz2"
  rm -f image.tar.bz2
fi

echo "[i] save the image (csp-openam:1.0) into file (image.tar)"
docker save csp-openam:1.0 -o image.tar
echo "[i] compress the saved image (image.tar) into (image.tar.bz2)"
bzip2 -9 image.tar
echo "[i] remove the saved uncompressed image (image.tar)"
rm -f image.tar
echo "[i] done."
