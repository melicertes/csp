#!/bin/sh

echo "[i] initializing RT_SiteConfig.pm for this container"


sed -i "s@__RT_WEB_URL__@https//rt.$DOMAIN@g" /opt/rt4/etc/RT_SiteConfig.pm
sed -i "s@__RT_WEB_DOMAIN__@rt.$DOMAIN@g" /opt/rt4/etc/RT_SiteConfig.pm

