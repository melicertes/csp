#!/bin/bash

# Apache gets grumpy about PID files pre-existing
rm -f /usr/local/apache2/logs/httpd.pid
echo "Executing apache with -f /etc/apache2/httpd.conf `date`"
httpd -D FOREGROUND -f /etc/apache2/httpd.conf
echo "Exited at `date` with $?"
