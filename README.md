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

## Development

### CI/CD pipeline

This plugin uses GitHub actions to build and deploy the plugin to the Gradle Plugin Portal.
The workflow is defined in `.github/workflows/build-and-deploy.yaml`and triggered on every push 
to the `main` branch and on every pull request.

### Dependabot
This repository uses [dependabot](https://dependabot.com/) to keep dependencies up to date. 
Dependabot is configured in `.github/dependabot.yaml`.

### Release process

The workflow uses the[release-please-action from Google](https://github.com/googleapis/release-please-action). 

> Release Please assumes you are using Conventional Commit messages.
>
> The most important prefixes you should have in mind are:
>
> fix: which represents bug fixes, and correlates to a SemVer patch.
>
> feat: which represents a new feature, and correlates to a SemVer minor.
>
> feat!:, or fix!:, refactor!:, etc., which represent a breaking change (indicated by the !) and will result in a SemVer major.

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
The required secrets for the GitHub actions are stored in the repository settings under "Secrets and variables" -> "Actions".
Currently, they are not managed as code. 

### Build and test the plugin
```
./gradlew build
./gradlew :test:generateGdprDocumentation
```
> [!NOTE]
> due to a bug in gradle regarding composite builds you cannot run `./gradlew clean build`, but you need to run
this as two separate commands
