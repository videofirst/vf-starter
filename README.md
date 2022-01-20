# [VF Starter](https://starter.videofirst.io) &middot; [![GitHub license](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/videofirst/vf-starter/blob/master/LICENSE) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/videofirst/vf-starter/blob/README.md)

Generates [VFA (Video First Automation)](https://github.com/videofirst/vfa) projects.

## UI

VF-Starter is a small web application (built using [Micronaut Framework](https://micronaut.io/)) which generate VFA
projects and enables users to start their automation journey as quickly and easily as possible.

To get started, visit [VF Starter](https://starter.videofirst.io), fill in the parameters and click the **Generate ZIP**
button to generate and download your project.

![Screenshot of VF Starter](docs/vf-starter-screenshot.png)

A preview option is also available to examine the files before downloading.

## API

You can also generate projects in your terminal using `curl` commands: -

```
$ curl https://starter.vieofirst.io/demo.zip -o demo.zip
$ unzip demo.zip -d demo
$ cd demo
$ ./gradlew run
```

Run `curl https://starter.videofirst.io` for more info on how to use the API.

## Docs

API documentation is available: -

* [Swagger / OpenAPI Doc](https://starter.videofirst.io/api-docs)

## License

VFA is published under the Apache License 2.0, see https://www.apache.org/licenses/LICENSE-2.0
for the full licence.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)
