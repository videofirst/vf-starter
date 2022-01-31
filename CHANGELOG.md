# v2022.1

* Micronaut web application which generates VFA projects using a "starter" file (YAML file with parameters + files).
* Service layer which parses / validates starter file and generate previews / ZIP files.
* API controllers which expose REST endpoints to display help, preview project files and generate a project ZIP.
* Controller `/ui` endpoint for user interface using Micronaut Freemarker Views - provides a useful way to set  
  parameters, visualise `curl` commands, download as ZIP and preview source files (using TreeJS / Prism source  
  highlighter).
* Controller `/docs` endpoint which uses Swagger / OpenAPI to provide ability to see / and try REST API documentation.
* Micronaut based integration tests to test API, UI and API docs endpoints.
* Add SSL / https support along with a script to generate a self-signed cert for integration testing.
* Dockerize application with Dockerfile / docker compose (support for port 443 / nginx re-direct).
* Add CLI support if arguments are passed to application and update service layer to write directly to a folder.
* Increase integration tests for different scenarios including prefix / setting parameters including new CLI mode.
* Release to https://start.videofirst.io