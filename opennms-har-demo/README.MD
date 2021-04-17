# OpenNMS web site monitoring har polling demo

## compile jira/NMS-13190 branch and create images

I have not changed the OpenNMS opennms-container within this branch but have provided these instructions to allow you to create a separate container which has firefox installed.

check out branch jira/NMS-13190 (https://github.com/OpenNMS/opennms/tree/jira/NMS-13190)

1. compile opennms

In opennms folder, compile as normal using
```
time (./clean.pl && ./compile.pl -U -DskipTests && ./assemble.pl -Dopennms.home=/opt/opennms -DskipTests)

```
This will build opennms and create a tarball of opennms in opennms/target.

2. build a standard docker image containing jira/NMS-13190

To simply build a docker image of this tarball, run the simple-horizon-image-build.sh script in the folder above (parent) of your checked out opennms. 

This will create a docker image of opennms tagged opennms/horizon:jira_NMS-13190 which can be used for the next step.

3. create image with horizon  horizon, headless firefox and selenium driver

To create a docker image including opennms horizon, firefox and selenium driver

In this folder run 
```
docker build --tag="opennms/horizon:HAR-FIREFOX" .

```
this will create an image with firefox and selenium installed based on the Dockerfile in this folder.

you can export this image using
```
docker image save "opennms/horizon:HAR-FIREFOX" -o horizon_HAR-FIREFOX.oci

```


## docker compose

The docker compose script expectes the opennms/horizon:HAR-FIREFOX to already be built

to start use docker compose

```
docker-compose up -d
```

opennms will take about 5 minutes to come up

you can follow the logs using

```
docker-compose logs -f horizon
```

you can also enter the container using
```
docker-compose exec horizon bash
```
you can check if the selenium poller is working by using

```
tail -f /logs/selenium_groovy.log
```

You can also follow logs inside the opennms karaf container using

```
ssh -o UserKnownHostsFile=/dev/null -p 8101 admin@localhost
password admin

once inside karaf use
admin@opennms> log:tail
```

## Accessing OpenNMS outside docker:

password opennms = admin, admin

http://localhost:8980/opennms

## setting up har polling
to start polling a web site and saving data to elastic, you need to 

a) create a node with an interface

b) add a SeleniumHar service to the interface

c) add metadata to the service using the key serviceurl e.g. serviceurl=https://www.solent.ac.uk                                                                                                           

# kibana

kibana is available at http://localhost:5601

it should pick up the opennmshardata index once you are collecting from sites

Some pre-defined visualisations are in kibana-export.json

## grafana

accessing grafana

http://localhost:3000

username admin
password mypass

when inside grafana for to settings and

1. install opennms helm plugin
2. create an opennms performance data source and entities data source with the settings

url: http://horizon:8989/opennms

auth username admin password admin




