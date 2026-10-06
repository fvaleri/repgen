[![build](https://github.com/fvaleri/repgen/actions/workflows/main.yml/badge.svg)](https://github.com/fvaleri/repgen/actions/workflows/main.yml)
[![release](https://img.shields.io/github/release/fvaleri/repgen.svg)](https://github.com/fvaleri/repgen/releases/latest)
[![license](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Repgen

Repgen is a general purpose report generation engine built on top of JasperReports library.

- The internal database contains the model definitions and dynamic content (model-name == template-name).
- Using JasperStudio tool you can create templates containing both report style and static content.
- All reports are built by a multi-threaded scheduler, and they can be rebuilt at any time.
- The engine supports embedded fonts (PDF/A spec) for optimal rendering on every system.
- The REST API can be used to request new report builds, search and download generated reports.

## Build from source

```sh
# Build and run
mvn package
java -jar target/repgen-*.jar

# Run tests
mvn verify

# Create binaries
mvn package -Passembly
```

## REST API

```sh
# Health check
curl -s http://localhost:8080/repgen/api/health

# Scheduler commands
curl -s http://localhost:8080/repgen/api/scheduler
curl -sX POST http://localhost:8080/repgen/api/scheduler/start
curl -sX POST http://localhost:8080/repgen/api/scheduler/stop

# Build request
curl -sX POST -H "Content-Type: application/json" \
  -d @src/test/resources/simple-letter.json http://localhost:8080/repgen/api/reports
curl -sX POST -H "Content-Type: application/json" \
  -d @src/test/resources/simple-table.json http://localhost:8080/repgen/api/reports

# Retrieve reports
curl -s http://localhost:8080/repgen/api/reports
curl -s http://localhost:8080/repgen/api/reports/1
curl -s http://localhost:8080/repgen/api/reports/1 | jq -r ".content" | base64 -d > test.pdf

# Search reports
curl -sX POST -H "Content-Type: application/json" \
  -d '{"name":"80c9d32"}' http://localhost:8080/repgen/api/reports/search
curl -sX POST -H "Content-Type: application/json" \
  -d '{"from":"2015-05-15", "to":"2015-06-15"}' http://localhost:8080/repgen/api/reports/search
```
