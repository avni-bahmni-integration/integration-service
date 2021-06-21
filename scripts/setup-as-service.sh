#!/usr/bin/env bash
# Generated files: start-ab-integrator.sh
set -e

if [[ $(id -u) -ne 0 ]] ; then echo "root/sudo privileges required" ; exit 1 ; fi

THIS_DIR=$(readlink -f $(dirname "$0"))
SCRIPTS_DIR=$(dirname ${THIS_DIR})
PROJECT_DIR=$(dirname ${SCRIPTS_DIR})
START_AB_I=${PROJECT_DIR}/start-ab-integrator.sh
SERVICE=abi.service
JAVA_PATH=

echo "#!/bin/bash
export BUGSNAG_API_KEY=
export AVNI_API_URL=https://app.avniproject.org
export AVNI_API_USER=avni-to-bahmni@ashwini
export BAHMNI_OPENMRS_API_URL=http://localhost:8050
export BAHMNI_OPENMRS_API_USER=avni_integration_txdata_admin
export INT_SCHEDULE_CRON=0 0 1 * * ?
export INT_APP_TASKS=Subject
export INT_DB_NAME=bahmni_avni
export INT_APP_FIRST_RUN=
%JAVA_PATH%/bin/java -jar integrator-0.0.1-SNAPSHOT.jar" > ${START_AB_I}
chmod 755 ${START_AB_I}
chown -R bahmni:bahmni ${START_AB_I}
echo "Generated ${START_AB_I} ..."

sed -e "s;%WORKING_DIR%;${PROJECT_DIR};g" ${THIS_DIR}/${SERVICE}.template > /etc/systemd/system/${SERVICE}
echo "Created /etc/systemd/system/${SERVICE} ..."

echo "systemctl daemon-reload ..."
systemctl daemon-reload
systemctl enable ${SERVICE}
