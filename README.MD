## Assetto Corsa Telemetry Service
Expose telemetry data in a graphical way to analyze it in real-time with Grafana dashboards

![](https://github.com/iimetra/assetto-corsa-telemetry-service/workflows/Master%20Branch/badge.svg)

Demonstration:

[![Watch the video](https://img.youtube.com/vi/4r4DgFnb5Tk/hqdefault.jpg)](https://youtu.be/4r4DgFnb5Tk)

## Installation
### Pre-requirements
1. Install [Docker](https://www.docker.com)
2. Install [Maven](https://maven.apache.org)
3. Put local IP address of your PS4 system into `.env` file as `GAME_SERVER_IP_ADDRESS`
### Run
1. Start your Assetto Corsa game-session
2. Execute the following commands to start up the web service:
```bash
mvn clean install
docker-compose up -d
```
3. Visit the [Grafana](http://localhost:3000) spun out on localhost and sign in with the admin / admin credentials
4. Open Telemetry Dashboard from the Home folder
5. Enjoy the telemetry diagrams in real-time

For a new session the following command has to be executed in order to reconnect to the server:
```bash
docker-compose restart act-app
```

## Workflow Diagram
![Workflow Diagram](diagram/workflow-diagram.png?raw=true)

## Technologies used
1. [Apache Camel](https://camel.apache.org) to route payloads from UDP game-server to the InfluxDB
2. [Spring Boot](https://spring.io/projects/spring-boot) as application container and Dependency Inversion
3. [InfluxDB](https://www.influxdata.com) as time series DB
4. [Grafana](https://grafana.com) as data visualizer
5. [Assetto Corsa Telemetry Client for Java](https://github.com/iimetra/assetto-corsa-telemetry-4j) to connect to UDP game-server on PS4/PC

## License
MIT License

Copyright (c) 2021 Artemii Morozov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
