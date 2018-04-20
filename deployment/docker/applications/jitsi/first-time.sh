echo "Dockerized jvb/openfire run as nobody... preparing volume"

docker volume create JitsiDataVolume
docker volume ls

echo "Cleaning volume content..."
RMC=$(docker run -d --rm -v JitsiDataVolume:/mnt csp-jitsiopenfire:1.0p2 sh -c "rm -rf /mnt/* && cp -R /usr/share/openfire/* /usr/share/openfire/.in* /mnt/ && ls -lrta /usr/share/openfire && ls -lrta /mnt")
docker wait $RMC
echo "Initializing volume..."
UNTARC=$(docker run -d --rm -v JitsiDataVolume:/mnt -v "$(pwd)":/data busybox:latest sh -c "tar xvfj /data/directories.tar.bz2 -C /mnt && mkdir -p /mnt/docker_scripts && cp /data/docker_scripts/* /mnt/docker_scripts")
docker wait $UNTARC
FIXC=$(docker run -d --rm -v JitsiDataVolume:/mnt -v SSLDatavolume:/data busybox:latest sh -c "mkdir -p /mnt/security && cp /data/server/csp-external-jitsi.jks /mnt/security/keystore")
docker wait $FIXC
FIXC2=$(docker run -d --rm -v JitsiDataVolume:/mnt -v SSLDatavolume:/data busybox:latest sh -c "chmod 777 /mnt/security/keystore && chown -R nobody: /mnt &&  chmod 777 /mnt/docker_scripts && chmod 777 /mnt/docker_scripts/*sh")
docker wait $FIXC2

echo "$RMC + $UNTARC + $FIXC + $FIXC2 == 0 - Should be right." 

