#!/bin/bash

# Apache gets grumpy about PID files pre-existing
rm -f /usr/local/apache2/logs/httpd.pid
echo "Executing apache with -f /etc/apache2/httpd.conf"
httpd -D FOREGROUND -f /etc/apache2/httpd.conf


