
function prepareModule() {
  DIR="$1"
  MODULE="${DIR%?}"
  WD=$(pwd)
  FNAME="../csp-$MODULE-`date +"%Y%m%d"`.zip"
  cd "$DIR"
  rm -f "$FNAME"
  if [ ! -f manifest.json ];
  then
     echo "manifest not found, creating a dummy 1.1"
     echo '{"format":1.1}' > manifest.json
  fi

  if [ -f "saveImage.sh" ] ;
  then
     chmod +x ./saveImage.sh
     echo "Executing saveImage to save docker images...."
     sh ./saveImage.sh
  fi
  echo "compressing packages"
  zip -9 -r  "$FNAME" * &>/dev/null
  R=$?
  #rm manifest.json #not necessary to stay here
  cd $WD
  echo "Done - zip $FNAME created, zip returned $R"
  echo "Created $FNAME on `date`" >> module-creation.log
  return $R
}

