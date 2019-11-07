cp ../../../../csp-apps/vcbridge/vcb-admin/target/vcb-admin-*-SNAPSHOT-exec.jar ./vcb-admin-exec.jar
cp ../../../../csp-apps/vcbridge/vcb-teleconf/target/vcb-teleconf-*-SNAPSHOT-exec.jar ./vcb-teleconf-exec.jar


docker build -t csp-jitsimeet:1.0 -f Dockerfile.jitsimeet .

