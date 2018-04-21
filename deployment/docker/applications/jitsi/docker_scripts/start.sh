set -e
cd /usr/share/openfire/docker_scripts
: ${POSTGRES_DOCR_PORT?Need a value for POSTGRES_DOCR_PORT}
: ${POSTGRES_USER?Need a value for POSTGRES_USER}
: ${POSTGRES_PASSWORD?Need a value for POSTGRES_PASSWORD}
XXXDBHOSTXXX=${XXXDBHOSTXXX-localhost}

echo "Jitsi[DEBUG]: psql -U ${POSTGRES_USER} -W${POSTGRES_PASSWORD}";
# Set password for psql
export PGPASSWORD=${POSTGRES_PASSWORD}; 
# Check if database jitsi exists
if psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -lqt | cut -d \| -f 1 | grep -qw jitsi; then
  echo "Jitsi[INFO]: Detected database exists..."
else
  echo "Jitsi[INFO]: Installing database for jitsi..."
  # Copy the original templates
  cp openfire.xml openfire.xml.tmp
  cp openfire_dump.sql openfire_dump.sql.tmp
  
  # replcate values in both copied templates 
  ./replace_values.sh openfire.xml.tmp
  ./replace_values.sh openfire_dump.sql.tmp
  
  # Create database, tables etc. and insert data
  psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} -c "CREATE DATABASE jitsi";
  psql -U${POSTGRES_USER} -h ${XXXDBHOSTXXX} "jitsi" < openfire_dump.sql.tmp
  
  # Copy new openfire configuration to openfire deployment
  cp openfire.xml.tmp ../conf/openfire.xml

  # Remove temp files
  rm openfire.xml.tmp
  rm openfire_dump.sql.tmp
fi
echo "Jitsi[INFO]: Launching openfire server..."
/usr/share/openfire/bin/openfire run
