#!/bin/bash


function agentConfiguration() {

    if [ -d "/web_agents/apache24_agent/instances/agent_$1" ]; then
        echo "$1 OpenAM Agents already exists."
        return 0
    fi

    if [ ! -f /web_agents/.agt ]; then
        echo "Initialize First[$1] OpenAM Agents"
        mkdir -p /web_agents/
        local count=0
        echo "password" > /opt/pwd

        echo "Starting Agent configuration...."
        while [ $count -lt 5 ]; do
        /web_agents/apache24_agent/bin/agentadmin --s \
              "/etc/apache2/httpd.conf" \
              "http://csp-oam:8080/openam" \
              "https://$1.$DOMAIN:443" \
              "/" \
              "WebAgent_$1" \
              "/opt/pwd" \
              --changeOwner \
              --acceptLicence

        local ret=$?
        if [ $ret -eq 0 ]; then
          echo "Agent configuration SUCCESS"
          touch /web_agents/.agt
          echo $1 > /web_agents/.agt
          chmod -R 777 /web_agents/apache24_agent
          chown -R root.root /web_agents/apache24_agent
          sed -i "s/AmAgent/#AmAgent/g" /etc/apache2/httpd.conf

          mv /web_agents/apache24_agent/instances/agent_1 /web_agents/apache24_agent/instances/agent_$1

          sed -i "s/agent_1/agent_$1/g" /web_agents/apache24_agent/instances/agent_$1/config/agent.conf
          sed -i "s/WebAgent_1/WebAgent_$1/g" /web_agents/apache24_agent/instances/agent_$1/config/agent.conf

          count=11 #we get out of the loop this way
          return 0
        else
          echo "Configuration has failed; sleeping to retry in 10sec [$count / 5]"
          count=$(( $count + 1 ))
          sleep 10
        fi
        done

        return 1
    else
        echo "Initialize $1 OpenAM Agent"
        service=$1
        newdomain=$1.$DOMAIN
        firstService=$(cat /web_agents/.agt)
        firstDomain=$firstService.$DOMAIN

        cd /web_agents/apache24_agent/instances

        cp -r agent_${firstService} agent_${service}
        sed -i "s/$firstDomain/$newdomain/g" /web_agents/apache24_agent/instances/agent_${service}/config/agent.conf
        sed -i "s/agent_${firstService}/agent_${service}/g" /web_agents/apache24_agent/instances/agent_${service}/config/agent.conf
        sed -i "s/WebAgent_${firstService}/WebAgent_${service}/g" /web_agents/apache24_agent/instances/agent_${service}/config/agent.conf

        return 0
    fi


# reaching here is a failure!
    return 1
}

service=$1
if [ -z "$service" ]
  then
    echo "Fatal Error: No argument supplied."
    exit -1
fi

echo "[i] Working with input: $service"

echo "[i] $service=>WebAgent_$service"
service=`echo "$service" | tr '[:upper:]' '[:lower:]'`

agentConfiguration $service
exit $?
