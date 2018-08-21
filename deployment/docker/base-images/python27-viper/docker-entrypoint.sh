#!/usr/bin/env bash

while [ ! -f /run/secrets/authkey ];
do echo "Waiting..." ;
sleep 1 ;
done

sed -i "s@misp_url =.*@misp_url = $(echo "$MISP_LOCAL_DOMAIN")@g" /home/viper/viper/viper.conf
sed -i "s/misp_key =.*/misp_key = $(cat /run/secrets/authkey)/g" /home/viper/viper/viper.conf

python3 viper-web -H 0.0.0.0 -p 8083

