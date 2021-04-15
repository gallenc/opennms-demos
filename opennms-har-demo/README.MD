# ALTERANTIVE CONFIGURATION - OPENNMS 27

## docker compose

to start use docker compose

```
docker-compose up -d
```

opennms will take about 5 minutes

you can follow logs using

```
docker-compose logs -f horizon
```

You can also follow inside opennmskaraf container using

```
ssh -o UserKnownHostsFile=/dev/null -p 8101 admin@localhost
password admin

once inside karaf use
admin@opennms> log:tail
```

## Accessing OpenNMS outside docker:

password opennms = admin, admin

http://localhost:8989/opennms


## grafana

accessing grafana

http://localhost:3000

username admin
password mypass

when inside grafana for to settings and

1. install opennms helm plugin
2. create an opennms performance data source and entities data source with settings

url: http://horizon:8989/opennms

auth username admin password admin

3. import a dashboard using the txPowerDashboard.json  file provided


