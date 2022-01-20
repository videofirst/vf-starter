# v2022.1

* Micronaut web application which generates VFA projects using a "starter" file (YAML file with parameters + files).
* Service layer which parses / validates starter file and generate previews / ZIP files.
* API controllers which expose REST endpoints to display help, preview project files and generate a project ZIP.
* Added `/ui` endpoint for user interface using Micronaut Freemarker Views - provides a useful way to set parameters,
  visualise `curl` commands, download as ZIP and preview source files (using TreeJS / Prism source highlighter).
* Added `/docs` endpoint which uses Swagger / OpenAPI to provide ability to see / and try REST API documentation.
* Added Micronaut integration tests to test API, UI and API docs endpoints.