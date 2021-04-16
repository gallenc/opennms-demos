#!/bin/bash
# set -x 
echo create docker image from opennms tarball. Run this script in the directory above opennms git repository

branch=$(git -C ./opennms branch --show-current)
name="${branch//'/'/"_"}"
echo creating docker image  $branch  in $name.oci

cp -v opennms/target/*SNAPSHOT.tar.gz  opennms/opennms-container/horizon/tarball/

echo docker build --tag="opennms/horizon:$name" opennms/opennms-container/horizon/
docker build --tag="opennms/horizon:$name" opennms/opennms-container/horizon/

echo docker image save "opennms/horizon:$name" -o opennms/opennms-container/horizon/images/horizon_$name.oci
docker image save "opennms/horizon:$name" -o opennms/opennms-container/horizon/images/horizon_$name.oci

