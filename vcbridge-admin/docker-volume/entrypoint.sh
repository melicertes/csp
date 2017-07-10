set -e
# server settings
VCBRIDGEADMIN_PORT=${VCBRIDGEADMIN_PORT-8080}

# database settings
: ${POSTGRES_USER? Need a value for POSTGRES_USER}
: ${POSTGRES_PASSWORD? Need a value for POSTGRES_PASSWORD}
: ${POSTGRES_DOCR_PORT? Need a value for POSTGRES_DOCR_PORT}
XXXDBHOSTXXX=${XXXDBHOSTXXX-localhost}

# openfire settings
: ${DOMAIN? Need a value for DOMAIN, this is about openfire domain, dont confuse with vcbridge amdin domain}
: ${XXXADMINCONSOLESECUREPORTXXX? Need a value for XXXADMINCONSOLESECUREPORTXXX}
: ${XXXADMINPASSXXX? Need a value for XXXADMINPASSXXX}
# All the above variables are used in application-prod.properties

echo "VCBridgeAdmin[DEBUG]: psql -U ${POSTGRES_USER} -W${POSTGRES_PASSWORD} -h${XXXDBHOSTXXX}";
# Set password for psql
export PGPASSWORD=${POSTGRES_PASSWORD}; 
# Check if database vcbdrige exists
if psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -lqt | cut -d \| -f 1 | grep -qw jitsi; then
  echo "VCBridgeAdmin[INFO]: Detected database 'vcbdrige' exists..."
else
  echo "VCBridgeAdmin[INFO]: Creating database 'vcbdrige'..."
  psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -c "CREATE DATABASE vcbridge";
fi
echo "VCBridgeAdmin[INFO]: Launching VCBridge server..."
java -jar -Dspring.profiles.active=prod /application.jar