import org.jreleaser.model.Active

/*
 *  Copyright 2025 TB Digital Services GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

plugins {
    kotlin("jvm")
    `java-library`
    id("maven-publish")
    id("org.jreleaser") version "1.19.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AMAZON
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "core"
            from(components["java"])

            pom {
                name.set("core")
                description.set("Core annotations and models gdpr documentation plugin" )
                url.set("https://github.com/rio-cloud/gradle-gdpr-documentation-plugin")
                inceptionYear.set("2025")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://spdx.org/licenses/Apache-2.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("rio-cloud")
                        name.set("RIO – The Logistics Flow")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/rio-cloud/gradle-gdpr-documentation-plugin.git")
                    developerConnection.set("scm:git:ssh://git@github.com/rio-cloud/gradle-gdpr-documentation-plugin.git")
                    url.set("https://github.com/rio-cloud/gradle-gdpr-documentation-plugin")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    // As of today one releaser must be presented even if this is a no-op configuration
    // We only use it for signing and deploying to Maven Central
    // see https://github.com/jreleaser/jreleaser/discussions/1725#discussioncomment-13888116
    release {
        github {
            skipRelease = true
        }
    }
    // required since this project is a subproject
    gitRootSearch = true
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(".gradle/build/staging-deploy")
                }
            }
        }
    }
}
