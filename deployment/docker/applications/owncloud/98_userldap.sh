#!/usr/bin/env bash


#expected variables

OAM_HOST="${OAM_DOCR_HOST:-csp-oam}"
OAM_AGENT="${OAM_AGENT:-cn=Directory Manager}"
OAM_AGPW="${OAM_AGPW:-11111111}"

OAM_BASE="${OAM_BASE:-dc=demo1-csp,dc=athens,dc=intrasoft-intl,dc=private}"

if [ "xx$FQDN" != "xx" ];
then
  echo "Domain is $FQDN, fixing LDAP Base..."
  FULL="$FQDN"
  OLDIFS="${IFS}"
  IFS=. components=(${FULL})
  IFS="${OLDIFS}"
  for i in "${components[@]}" 
  do 
    V="dc=$i,"
    F=($F$V)
    #echo "$i V=$V F=$F"; 
  done
  OAM_BASE=${F%?}
  echo "Fixed: Domain to LDAP is $OAM_BASE"
fi

echo "OAM_DOCR_HOST=$OAM_DOCR_HOST"
echo "OAM_HOST=$OAM_HOST"
echo "OAM_AGENT=$OAM_AGENT"
echo "OAM_AGPW=(not shown)"
echo "OAM_BASE=$OAM_BASE"

TP="/mnt/data/.ldapconfig-v360"

function performLDAPConfig() {

  echo "Enabling LDAP"
  occ app:enable user_ldap

  echo "Creating empty config"
  occ ldap:create-empty-config 

  echo "LDAP host ${OAM_HOST} and Agent ${OAM_AGENT}"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapHost "${OAM_HOST}"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapPort 50389
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapAgentName "${OAM_AGENT}"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapAgentPassword "${OAM_AGPW}"

  echo "LDAP base"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapBase ${OAM_BASE}
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapBaseGroups ${OAM_BASE}
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapBaseUsers ${OAM_BASE}

  echo "LDAP filters"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapUserFilter "(|(objectclass=inetOrgPerson))"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapUserFilterObjectclass "inetOrgPerson"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapEmailAttribute "mail"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapUserDisplayName2 "cn"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapExpertUsernameAttr "cn"

  echo "LDAP group"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapGroupFilter "(|(cn=csp-admin)(cn=csp-user))"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapGroupFilterGroups "csp-admin;csp-user"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapLoginFilter "(&(|(objectclass=inetOrgPerson))(|(uid=%uid)(|(mailPrimaryAddress=%uid)(mail=%uid))(|(cn=%uid))))"

  echo "LDAP set active"
  su-exec www-data php /var/www/owncloud/occ ldap:set-config s01 ldapConfigurationActive 1

  echo "COMPLETE CONFIGURATION" 
  echo "----------------------"
  echo ""
  echo ""
  occ ldap:show-config s01
  echo "----------------------"
  echo ""
  echo ""
  echo "Setting host ${OWNCLOUD_DOMAIN}:${OWNCLOUD_HTTPS_PORT} ... "
  su-exec www-data php /var/www/owncloud/occ config:system:set overwritehost --value=${OWNCLOUD_DOMAIN}:${OWNCLOUD_HTTPS_PORT}
  echo "Setting protocol https ... "
  su-exec www-data php /var/www/owncloud/occ config:system:set overwriteprotocol --value=https
  echo ""
  echo "done."
  echo ""

}

if [ -f "$TP" ]; then
    echo "LDAP Configuration has been completed already."
else 
    echo "LDAP Config about to start: "
    performLDAPConfig
    echo "LDAP Config now complete"
    touch "$TP"
fi
