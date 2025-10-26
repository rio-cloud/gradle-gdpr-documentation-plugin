# gradle-gdpr-documentation-plugin

![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cloud.rio.gdprdoc)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/rio-cloud/gradle-gdpr-documentation-plugin/build-and-deploy.yaml)

Gradle plugin to generate data classification documentation (needed for the GDPR documentation) for your project based
on annotations on data classes and/or from configuration files.

## Disclaimer

> [!WARNING]
> This plugin will only create parts of the GDPR documentation. If you want to use this for your own GDPR documentation,
> make sure to classify the data according to your own requirements. RIO is not responsible for your documentation.

> [!NOTE]
> RIO maintains this repository for their internal documentation. If you need different / additional functionality,
> please fork the project.

## Usage

### Classify your data

There are two ways to classify data:

1. Annotate your data classes with the provided annotations
2. Provide configuration files

You can also combine both approaches. In case of conflicts, the configuration files will have precedence over the
annotations.

See [example project](./test) for examples of both approaches.

#### Annotations

You can annotate your data classes with the following annotations (defined
in [GdprData](./core/src/main/kotlin/cloud/rio/gdprdoc/annotations/GdprDoc.kt)), describing the data flow and purpose:

- `@Incoming` to mark incoming data (e.g. from a REST API)
- `@Outgoing` to mark outgoing data (e.g. as a response to an API call from another service)
- `@Persisted` to mark persisted data (e.g. in a database)
- `@ReadModel` to mark read models (e.g. in a CQRS setup)

You can also use multiple of these annotations on a single class. Note that `@ReadModel` will automatically classify the
data both as incoming and as persisted.

You can document the PII level of each field in the class with the `@Field` annotation which accepts the following enum
values as parameter:

- `PII`
- `PSEUDONYM`
- `NON_PII`

In case you have a nested class structure, you can mark fields with a nested type with the `@NestedType` annotation. The plugin
will then recursively analyze the class structure and include its fields in the documentation. You need to add the
`@Field` annotation to the nested type class fields as well.

#### Configuration files

You can also provide one or multiple configuration files in yaml format to classify your data.

Each configuration file consists of a list of classes, containing the fully qualified class name, one or multiple blocks
describing the data flow and purpose of the data (analogous to the annotations), and a list of fields with their PII
level.

The following example illustrates the structure of the configuration file:

```yaml
classes:
  - className: cloud.rio.example.adapter.restclient.IncomingDTO
    incoming:
      whatToDo: Forward via API
      whereFrom: Some external service
    fields:
      - name: id
        level: PSEUDONYM
      - name: name
        level: PII
      - name: description
        level: NON_PII
  - className: cloud.rio.example.adapter.rest.OutgoingDTO
    outgoing:
      sharedWith: Exposed via API
      why: Display in frontend
    fields:
      ...
  - className: cloud.rio.example.adapter.db.PersistedEntity
    persisted:
      retention: 6 months
      responsibleForDeletion: Automatic deletion job
      databaseIdentifier: arn:aws:dynamodb:region:accountId:table/persisted-entity
    fields:
      ...
  - className: cloud.rio.example.adapter.readmodel.ReadModel
    readModel:
      whatToDo: Persist in DB
      whereFrom: Some external service
      retention: 6 months
      databaseIdentifier: arn:aws:dynamodb:region:accountId:table/read-model
      responsibleForDeletion: Automatic deletion job
    fields:
      ...
```

### Apply and configure the plugin

To use the plugin, add the plugin to the plugins block of your `build.gradle.kts` file and add the core dependency to
the compile time classpath:

```kotlin
plugins {
    id("cloud.rio.gdprdoc") version "2.0.1"
}

dependencies {
    compileOnly("cloud.rio.gdprdoc:core:2.0.1")
}
```

You can configure the documentation generation task to

* change the output file name and location
* specify the configuration files to use
* include annotated classes from other projects
  The following example shows how to do this:

```kotlin
tasks {
    generateGdprDocumentation {
        // Change output
        markdownReport = file("docs/gdpr/gdpr-documentation.md")
        // Specify configuration files
        additionalGdprDataFiles.setFrom(
            fileTree("src/main/resources") { include("**/gdpr-documentation.yaml") },
        )
        // Include annotated classes from another project
        classpath.from(
            configurations.runtimeClasspath.get().filter {
                it.name.contains("some-other-project")
            }
        )
    }
}
```

By default, the output will be written to `build/reports/gdpr-documentation.md`, and no configuration files will be
used.

### Generate the documentation

Generate the documentation by running:

```
./gradlew generateGdprDocumentation
```

You find the documentation in `build/reports/gdpr-documentation.md` unless you configured a different location (see
above).

Make sure to enable PlantUML in your markdown renderer in your IDE to see the Data Flow Diagram.
Backstage also supports PlantUML, so it should work there without additional setup.

## Development

### CI/CD pipeline

This plugin uses GitHub actions to build and deploy the plugin to the Gradle Plugin Portal.
The workflow is defined in `.github/workflows/build-and-deploy.yaml`and triggered on every push
to the `main` branch and on every pull request.

### Dependabot

This repository uses [dependabot](https://dependabot.com/) to keep dependencies up to date.
Dependabot is configured in `.github/dependabot.yaml`.

### Release process

The workflow uses the [release-please-action from Google](https://github.com/googleapis/release-please-action).

> _release-please_ assumes you are using Conventional Commit messages.
>
> The most important prefixes you should have in mind are:
>
> fix: which represents bug fixes, and correlates to a SemVer patch.
>
> feat: which represents a new feature, and correlates to a SemVer minor.
>
> feat!:, or fix!:, refactor!:, etc., which represent a breaking change (indicated by the !) and will result in a SemVer
> major.

When release-please detects one or more conventional commits, it will create or update a pull request.
Once the pull request is merged, the workflow will create a new release and deploy the plugin.

### Manually trigger the release process

All commits not following the conventional commit format will be ignored by the release-please-action.
Examples include:

- updating documentation
- merge dependabot updates
- ...

To trigger the action simply create an empty commit following the conventional commit format, e.g.:

```
git commit --allow-empty -m "fix: prepare next release. Update dependencies"
git push
```

### Secret management

The required secrets for the GitHub actions are stored in the repository settings under "Secrets and variables" -> "
Actions".
Currently, they are not managed as code.

### Build and test the plugin

```
./gradlew clean build
```

### Build and test the example project

```
cd test
../gradlew clean build
```
