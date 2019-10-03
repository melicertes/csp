#!/bin/bash

set -e
verifyMispState(){
        echo "Waiting for mysql"
        until mysql -h"$MYSQL_HOST" -u root --password=$MYSQL_ROOT_PASSWORD  -e  "show databases"  &> /dev/null
        do
          printf "."
          sleep 1
        done

        echo -e "\nmysql ready"

        ret=`echo 'SHOW DATABASES;' | mysql -u root --password="$MYSQL_ROOT_PASSWORD" -h $MYSQL_HOST -P 3306 # 2>&1`

        if [ $? -eq 0 ]; then
                echo "Connected to database successfully!"
                found=0
                for db in $ret; do
                        if [ "$db" == "misp" ]; then
                                found=1
                        fi
                done
                if [ $found -eq 1 ]; then
                        echo "Misp Already Installed"
                        touch /opt/state/installed.tmp
                else
                        echo "Misp first run"

                fi
        else
                echo "ERROR: Connecting to database failed:"
                echo $ret
        fi

}

initMispConfig() {
  declare -g -A MISP_AUTO_SETTINGS
  CSP_ID_PREFIX="CSP::"
  #MISP_AUTO_SETTINGS["MISP.live"]="1"
  MISP_AUTO_SETTINGS["MISP.org"]=$CSP_ID_PREFIX$CSP_NAME
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_enable"]="0"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_header"]="CUSTOM_USER_ID"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_use_header_namespace"]="1"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_header_namespace"]="HTTP_"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_required"]="0"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_only_allow_source"]=""
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_name"]="External authentication"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_disable_logout"]="1"
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_custom_password_reset"]=""
  MISP_AUTO_SETTINGS["Plugin.CustomAuth_custom_logout"]="/logout/"
  MISP_AUTO_SETTINGS["Plugin.ZeroMQ_enable"]="1"
  MISP_AUTO_SETTINGS["Plugin.ZeroMQ_user_notifications_enable"]="1"
  MISP_AUTO_SETTINGS["Plugin.ZeroMQ_audit_notifications_enable"]="1"
  MISP_AUTO_SETTINGS["Plugin.ZeroMQ_event_notifications_enable"]="1"
}

updateMispConfig() {
  for key in "${!MISP_AUTO_SETTINGS[@]}"; do
    testvar=("$key")
    /var/www/MISP/app/Console/cake Admin setSetting "$key" "${MISP_AUTO_SETTINGS[$key]}" -q;
    sleep 0.1
    afterValue=$(/var/www/MISP/app/Console/cake Admin getSetting "$key" -q | python -c "import sys, json; print json.load(sys.stdin)['value']";)
    # Converting python booleans back to binary
    if [ "$afterValue" == "False" ]
    then
	    afterValue="0"
    elif [ "$afterValue" == "True" ]
    then
	    afterValue="1"
    fi

    if [ "${MISP_AUTO_SETTINGS[$key]}" == "$afterValue" ]
    then
	    echo -n "SUCCESS : "
    else
	    echo -n "FAILED  : "
    fi
    echo Setting $testvar as "${MISP_AUTO_SETTINGS[$key]}"
  done
}

verifyMispDBConfig() {
  cd /var/www/MISP/app/Config
  CHK=`diff -q database.default.php database.php >/dev/null ; echo $?`
  if [ $CHK -eq 0 ] 
  then 
   # MISP configuration
   echo "Creating MISP configuration files"
   cd /var/www/MISP/app/Config
   cp -a database.default.php database.php
   sed -i "s/localhost/$MYSQL_HOST/" database.php
   sed -i "s/db\s*login/misp/" database.php
   sed -i "s/8889/3306/" database.php
   sed -i "s/db\s*password/$MYSQL_MISP_PASSWORD/" database.php
  fi
}


verifyMispState

if [ ! -f /opt/state/installed.tmp ]; then
        echo "Container started for the fist time. Setup might time a few minutes. Please wait..."
        echo "(Details are logged in /tmp/install.log)"
        export DEBIAN_FRONTEND=noninteractive

        # If the user uses a mount point restore our files
        if [ ! -d /var/www/MISP/app ]; then
                echo "Restoring MISP files..."
                cd /var/www/MISP
                tar xzpf /root/MISP.tgz
                rm /root/MISP.tgz
        fi

        echo "Configuring postfix"
        if [ -z "$POSTFIX_RELAY_HOST" ]; then
                echo "POSTFIX_RELAY_HOST is not set, please configure Postfix manually later..."
        else
                postconf -e "relayhost = $POSTFIX_RELAY"
        fi

        # Fix timezone (adapt to your local zone)
        if [ -z "$TIMEZONE" ]; then
                echo "TIMEZONE is not set, please configure the local time zone manually later..."
        else
                echo "$TIMEZONE" > /etc/timezone
                dpkg-reconfigure -f noninteractive tzdata >>/tmp/install.log
        fi

        echo "Creating MySQL database"

        # Check MYSQL_HOST
        if [ -z "$MYSQL_HOST" ]; then
                echo "MYSQL_HOST is not set. Aborting."
                exit 1
        fi

        # Set MYSQL_ROOT_PASSWORD
        if [ -z "$MYSQL_ROOT_PASSWORD" ]; then
                echo "MYSQL_ROOT_PASSWORD is not set, use default value 'root'"
                MYSQL_ROOT_PASSWORD=root
        else
                echo "MYSQL_ROOT_PASSWORD is set to '$MYSQL_ROOT_PASSWORD'"
        fi

        # Set MYSQL_MISP_PASSWORD
        if [ -z "$MYSQL_MISP_PASSWORD" ]; then
                echo "MYSQL_MISP_PASSWORD is not set, use default value 'misp'"
                MYSQL_MISP_PASSWORD=misp
        else
                echo "MYSQL_MISP_PASSWORD is set to '$MYSQL_MISP_PASSWORD'"
        fi

        echo "Waiting for mysql"
        until mysql -h"$MYSQL_HOST" -u root --password=$MYSQL_ROOT_PASSWORD  -e  "show databases"  &> /dev/null
        do
          printf "."
          sleep 1
        done

        echo -e "\nmysql ready"

        ret=`echo 'SHOW DATABASES;' | mysql -u root --password="$MYSQL_ROOT_PASSWORD" -h $MYSQL_HOST -P 3306 # 2>&1`

        if [ $? -eq 0 ]; then
                echo "Connected to database successfully!"
                found=0
                for db in $ret; do
                        if [ "$db" == "misp" ]; then
                                found=1
                        fi
                done
                if [ $found -eq 1 ]; then
                        echo "Database misp found"
                else
                        echo "Database misp not found, creating now one ..."
                        cat > /tmp/create_misp_database.sql <<-EOSQL
create database misp;
grant usage on *.* to misp identified by "$MYSQL_MISP_PASSWORD";
grant all privileges on misp.* to misp;
EOSQL
                        ret=`mysql -u root --password="$MYSQL_ROOT_PASSWORD" -h $MYSQL_HOST -P 3306 2>&1 < /tmp/create_misp_database.sql`
                        if [ $? -eq 0 ]; then
                                echo "Created database misp successfully!"

                                echo "Importing /var/www/MISP/INSTALL/MYSQL.sql ..."
                                ret=`mysql -u misp --password="$MYSQL_MISP_PASSWORD" misp -h $MYSQL_HOST -P 3306 2>&1 < /var/www/MISP/INSTALL/MYSQL.sql`
                                if [ $? -eq 0 ]; then
                                        echo "Imported /var/www/MISP/INSTALL/MYSQL.sql successfully"
                                else
                                        echo "ERROR: Importing /var/www/MISP/INSTALL/MYSQL.sql failed:"
                                        echo $ret
                                fi
                                # service mysql stop >/dev/null 2>&1
                        else
                                echo "ERROR: Creating database misp failed:"
                                echo $ret
                        fi
                fi
        else
                echo "ERROR: Connecting to database failed:"
                echo $ret
        fi


        # Fix the base url
        if [ -z "$MISP_BASEURL" ]; then
                echo "No base URL defined, don't forget to define it manually!"
        else
                echo "Fixing the MISP base URL ($MISP_BASEURL) ..."
                sed -i "/baseurl/c\ 'baseurl'                        => '${MISP_BASEURL}'," /var/www/MISP/app/Config/config.php
        fi

        echo "Configuring MISP (ZMQ and SSO)..."
        sed -e "N;/\n.*'SecureAuth'/{r csp_configuration" -e "};P;D" -i /var/www/MISP/app/Config/config.php

        chown -R 33.33 /var/www/MISP/.gnupg

        # Generate the admin user PGP key
        echo "Creating admin GnuPG key"
        if [ -z "$MISP_ADMIN_EMAIL" -o -z "$MISP_ADMIN_PASSPHRASE" ]; then
                echo "No admin details provided, don't forget to generate the PGP key manually!"
        else
                echo "Generating admin PGP key ... (please be patient, we need some entropy)"
                cat >/tmp/gpg.tmp <<GPGEOF
%echo Generating a basic OpenPGP key
Key-Type: RSA
Key-Length: 2048
Name-Real: MISP Admin
Name-Email: $MISP_ADMIN_EMAIL
Expire-Date: 0
Passphrase: $MISP_ADMIN_PASSPHRASE
%commit
%echo Done
GPGEOF
                sudo -u www-data gpg --homedir /var/www/MISP/.gnupg --gen-key --batch /tmp/gpg.tmp >>/tmp/install.log
                rm -f /tmp/gpg.tmp
        fi

echo "Fixing permissions on /var/www/MISP/app/tmp/logs/"
chmod -R 777 /var/www/MISP/app/tmp/logs/

echo "User init"
/var/www/MISP/app/Console/cake user_init $MISP_ADMIN_EMAIL $MISP_ADMIN_PASSPHRASE

echo "User Update"
/var/www/MISP/app/Console/cake Password $MISP_ADMIN_EMAIL $MISP_ADMIN_PASSPHRASE

#CSP_USER="csp@integration.melicertes"
#echo "User $CSP_USER init"
#/var/www/MISP/app/Console/cake user_init $CSP_USER $MISP_ADMIN_PASSPHRASE
#
#echo "User Update"
#/var/www/MISP/app/Console/cake Password $CSP_USER $MISP_ADMIN_PASSPHRASE

sleep 10
echo "Init Authkey"
/var/www/MISP/app/Console/cake Authkey $MISP_ADMIN_EMAIL | tail  -1 > /run/secrets/authkey

sleep 10
echo "Applying MISP Server settings for CSP functionality..."
initMispConfig
updateMispConfig


        # Display tips
        cat <<__WELCOME__
Congratulations!
Your MISP docker has been successfully booted for the first time.
Don't forget:
- Reconfigure postfix to match your environment
- Change the MISP admin email address to $MISP_ADMIN_EMAIL

__WELCOME__

        touch /opt/state/installed.tmp
fi

verifyMispDBConfig


echo "Fixing permissions on /var/www/MISP/app/tmp/logs/"
chown -R 33.33 /var/www/MISP/app/tmp/logs/
chown -R 33.33 /var/www/MISP/app/Config/
chmod -R 777 /var/www/MISP/app/tmp/logs

# Start supervisord
echo "Starting supervisord"
cd /
exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
