# Repgen

Repgen is a general purpose report generation service built on top of JasperReports library.

- The internal repo contains model definitions and dynamic data (`model-name == template-name`)
- Using JaspertStudio you can create templates containing the report style and static data
- Reports are built by a multi-threaded scheduler and they can be rebuilt at any time
- PDF reports can have embedded fonts (PDF/A spec) for optimal rendering on every system
- There is a REST API to request new builds, search and download completed reports

## Build and run

```sh
mvn clean package
java -jar target/repgen-1.3.0-SNAPSHOT.jar
```

## REST API

Health check:

```sh
BASEURL="http://localhost:8080/repgen"
curl -s $BASEURL/health
```

Scheduler commands:

```sh
curl -s $BASEURL/api/scheduler
curl -sX POST $BASEURL/api/scheduler/start
curl -sX POST $BASEURL/api/scheduler/stop
```

Build request:

```sh
curl -sX POST -H "Content-Type: application/json" \
    -d @src/test/resources/simple-letter.json $BASEURL/api/reports
curl -sX POST -H "Content-Type: application/json" \
    -d @src/test/resources/simple-table.json $BASEURL/api/reports
```

Retrieve reports:

```sh
curl -s $BASEURL/api/reports
curl -s $BASEURL/api/reports/1
```

Search reports:

```sh
curl -sX POST -H "Content-Type: application/json" \
    -d '{"name":"80c9d32"}' $BASEURL/api/reports/search
curl -sX POST -H "Content-Type: application/json" \
    -d '{"from":"2015-05-15", "to":"2015-06-15"}' $BASEURL/api/reports/search
```
