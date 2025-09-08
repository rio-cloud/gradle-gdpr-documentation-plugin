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
    kotlin("plugin.serialization") version "1.9.0"
    id("com.gradle.plugin-publish") version "1.3.1"
    id("com.gradleup.shadow") version "8.3.9"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation("com.charleskorn.kaml:kaml:0.94.0")
    implementation(kotlin("stdlib"))
    implementation("io.github.classgraph:classgraph:4.8.162")
    implementation(project(":core"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AMAZON
    }
}

gradlePlugin {
    website = "https://company.rio.cloud"
    vcsUrl = "https://github.com/rio-cloud/gradle-gdpr-documentation-plugin"
    plugins {
        create("rio-gdpr-documentation-plugin") {
            id = "cloud.rio.gdprdoc"
            implementationClass = "cloud.rio.gdprdoc.GdprDocumentationPlugin"
            displayName = "RIO GDPR documentation plugin"
            description =
                "Gradle plugin to generate data classification documentation (needed for the GDPR documentation) for your project based on annotations on data classes"
            tags.set(listOf("gdpr", "documentation", "rio", "rio.cloud"))
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.shadowJar {
    /**
     * This removes the default `-all` classifier
     * Otherwise, we will observe the following error
     * Please configure the `shadowJar` task to not add a classifier to the jar it produces
     **/
    archiveClassifier.set("")
    minimize()
}
