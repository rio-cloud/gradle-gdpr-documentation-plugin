# gradle-gdpr-documentation-plugin

![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cloud.rio.gdprdoc)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/rio-cloud/gradle-gdpr-documentation-plugin/build-and-deploy.yaml)

Gradle plugin to generate data classification documentation (needed for the GDPR documentation) for your project based
on annotations on data classes.

## Disclaimer

> [!WARNING]
> This plugin will only create parts of the GDPR documentation. If you want to use this for your own GDPR documentation,
> make sure to classify the data according to your own requirements. RIO is not responsible for your documentation.

> [!NOTE]
> RIO maintains this repository for their internal documentation. If you need different / additional functionality, please fork the project.

## Development

```
./gradlew build
./gradlew :test:generateGdprDocumentation
```

(note that due to a bug in gradle regarding composite builds you cannot run `./gradlew clean build`, but you need to run
this as two separate commands)

## Usage

See [example project](./test).

Generate the documentation by running:
```
./gradlew generateGdprDocumentation
```
You find the documentation in `build/reports/gdpr-documentation.md`. It currently needs to be manually 
copied to `docs/gdpr-documentation.md`

Make sure to enable PlantUML in your markdown renderer in your IDE to see the Data Flow Diagram.
Backstage also supports PlantUML, so it should work there without additional setup.
