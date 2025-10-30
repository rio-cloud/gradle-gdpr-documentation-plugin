# Changelog

## [3.0.1](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v3.0.0...v3.0.1) (2025-10-30)


### Bug Fixes

* fix npe when trying to declare a nested type which is not wrapped in e.g. a list ([8cc2bf7](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/8cc2bf7fbdb6135a774fcfbe865d8f8565c6cae6))

## [3.0.0](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v2.1.1...v3.0.0) (2025-10-27)


### ⚠ BREAKING CHANGES

* remove links feature as this breaks architecture tests and is not required by security guild
* introduce required databaseIdentifier property for persisted & readModel definitions

### Features

* Add support for nested class structures via additional gdpr data files. ([765c9a0](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/765c9a09b58b7875482ab4810bbc8ad86f941567))
* Add support for nested class structures. ([55b4724](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/55b4724974d42fdf836fd15dd65d36558a61c865))
* introduce required databaseIdentifier property for persisted & readModel definitions ([c608a50](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/c608a501cf74c5492b1fb807eebb7aa66a9582ed))
* remove links feature as this breaks architecture tests and is not required by security guild ([875e492](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/875e49209212d0d54a5913ff2b4686f5e1fbae61))

## [2.1.1](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v2.1.0...v2.1.1) (2025-09-03)


### Bug Fixes

* filter out annotation classes themselves when scanning for annotated classes ([#29](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/issues/29)) ([5522cf3](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/5522cf30a54e4cc80cd6e976e17e00045b874345))

## [2.1.0](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v2.0.1...v2.1.0) (2025-09-02)


### Features

* Allow specifying additional GDPR data in yaml files ([b8c9ee7](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/b8c9ee78cb7137acb7365f620ebb69bbb30f5340))

## [2.0.1](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v2.0.0...v2.0.1) (2025-08-29)


### Bug Fixes

* wrong stagingRepositoryPath ([12d6d6a](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/12d6d6a08d39809f32d6df13de7b365c4c642e21))

## [2.0.0](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v1.0.3...v2.0.0) (2025-08-29)


### ⚠ BREAKING CHANGES

* deploy core JAR to Maven Central

### Bug Fixes

* deploy core JAR to Maven Central ([a8d045e](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/a8d045e7126ec88ab2fcdbfea58b9c2511bdbd7d))

## [1.0.3](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v1.0.2...v1.0.3) (2025-08-26)


### Bug Fixes

* minimize shadow jar ([8720ebf](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/8720ebf516e0af65ddc8273650957c782b5bc97c))
* specify plugin as compileOnly dependency as it is not needed in the runtime of the application ([4880d72](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/4880d722cddeb072424ff5d453d4360de2a5bd7f))

## [1.0.2](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v1.0.1...v1.0.2) (2025-08-07)


### Bug Fixes

* prepare next release. Update dependencies ([725335a](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/725335a93d8cf5ec720d6afeed70e424f6b3461e))

## [1.0.1](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v1.0.0...v1.0.1) (2025-08-06)


### Bug Fixes

* prepare next release ([b6d36cd](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/b6d36cd9d8d6065f9ee9f3adb4723cf3fe201542))

## [1.0.0](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.7...v1.0.0) (2025-08-06)


### ⚠ BREAKING CHANGES

* prepare for v1.0.0

### Features

* first stable release ([bf04f87](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/bf04f8726320a963258822347b4dfa2cf9f68844))

## [0.0.7](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.6...v0.0.7) (2025-08-06)


### Bug Fixes

* add missing permissions ([3fb5965](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/3fb596566804ab8c8dbaaca73e9c38093ce4e936))
* add missing step ([a0acb52](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/a0acb5296a40e89811c65ce21eb0479fa7d03a3a))
* add more debug logs. Try other condition for deploy ([047e3e8](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/047e3e8f28a7bf40d116b8fcbbd702fd18290422))
* remove wrong if expressions ([e6484f3](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/e6484f3a2dbbc124ab7137051f1e5cbaed318dc8))
* use setup from google example ([6d02d4b](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/6d02d4bb25125f9d1e64420f90cdae75b493254d))

## [0.0.6](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.5...v0.0.6) (2025-08-06)


### Bug Fixes

* add debug infos ([96c8435](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/96c8435c426af2021e84c251f253800f45879a2f))

## [0.0.5](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.4...v0.0.5) (2025-08-06)


### Bug Fixes

* separate build and deploy action ([fdb04f4](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/fdb04f46b2407c0dc33e0d1073174076e96554ed))

## [0.0.4](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.3...v0.0.4) (2025-08-06)


### Bug Fixes

* change condition ([769219f](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/769219fe1625ac7a09593553fa70c541890d2c9d))

## [0.0.3](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/compare/v0.0.2...v0.0.3) (2025-08-06)


### Bug Fixes

* change root project name to match repository name ([f080725](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/f08072523e0e538620c636f5deeb9d68171d6891))
* use correct multiline string ([6247abb](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/6247abbe8a04d09738dbe34b0c354abd8fca84e9))
* use project version instead of hardcoded version ([659fd90](https://github.com/rio-cloud/gradle-gdpr-documentation-plugin/commit/659fd90990b192a4d8532edee0fbc517f9ca755b))
