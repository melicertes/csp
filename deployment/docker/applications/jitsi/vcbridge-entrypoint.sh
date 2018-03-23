set -e
# server settings
VCBRIDGEADMIN_PORT=${VCBRIDGEADMIN_PORT-8080}
XXXMAXTASKRETRIESXXX=${XXXMAXTASKRETRIESXXX-2}
XXXWAITAFTERSUBMISSIONXXX=${XXXWAITAFTERSUBMISSIONXXX-15}

# email settings
: ${XXXMAILSERVERHOSTXXX? Need a value for XXXMAILSERVERHOSTXXX}
: ${XXXMAILSERVERPORTXXX? Need a value for XXXMAILSERVERPORTXXX}
: ${XXXMAILUSERNAMEXXX? Need a value for XXXMAILUSERNAMEXXX}
: ${XXXMAILPASSWORDXXX? Need a value for XXXMAILPASSWORDXXX}

# database settings
: ${POSTGRES_USER? Need a value for POSTGRES_USER}
: ${POSTGRES_PASSWORD? Need a value for POSTGRES_PASSWORD}
: ${POSTGRES_DOCR_PORT? Need a value for POSTGRES_DOCR_PORT}
XXXDBHOSTXXX=${XXXDBHOSTXXX-localhost}

# openfire settings
: ${DOMAIN? Need a value for DOMAIN, this is about openfire domain, dont confuse with vcbridge amdin domain}
JITSI_HOST=${HOST_IP}
: ${JITSI_HOST? Need a value for JITSI_HOST}
XXXADMINCONSOLESECUREPORTXXX=${XXXADMINCONSOLESECUREPORTXXX-9091}
XXXADMINPASSXXX=${XXXADMINPASSXXX-adminpas}


# All the above variables are used in application-prod.properties
export VCBRIDGEADMIN_PORT
export XXXMAXTASKRETRIESXXX
export XXXWAITAFTERSUBMISSIONXXX

export XXXMAILSERVERHOSTXXX
export XXXMAILSERVERPORTXXX
export XXXMAILUSERNAMEXXX
export XXXMAILPASSWORDXXX

export POSTGRES_USER
export POSTGRES_PASSWORD
export POSTGRES_DOCR_PORT
export XXXDBHOSTXXX
export DOMAIN
export JITSI_HOST
export XXXADMINCONSOLESECUREPORTXXX
export XXXADMINPASSXXX

echo "VCBridgeAdmin[DEBUG]: psql -U ${POSTGRES_USER} -W${POSTGRES_PASSWORD} -h${XXXDBHOSTXXX}";
# Set password for psql
export PGPASSWORD=${POSTGRES_PASSWORD}; 
# Check if database vcbdrige exists
if psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -lqt | cut -d \| -f 1 | grep -qw vcbridge; then
  echo "VCBridgeAdmin[INFO]: Detected database 'vcbdrige' exists..."
else
  echo "VCBridgeAdmin[INFO]: Creating database 'vcbdrige'..."
  psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -c "CREATE DATABASE vcbridge";
fi
echo "VCBridgeAdmin[INFO]: Launching VCBridge server..."
#java -jar -Denable.oam=false -Dspring.profiles.active=prod /application.jar
java -jar -Dspring.profiles.active=prod /application.jar
