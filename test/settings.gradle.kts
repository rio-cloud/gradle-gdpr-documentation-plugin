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

rootProject.name = "gdpr-doc-example"

// Resolve the plugin-under-development by id, without publishing
pluginManagement {
    includeBuild("..")  // path to the repo root that contains :plugin
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Use the local :core project instead of a published artifact
includeBuild("..") {
    dependencySubstitution {
        substitute(module("cloud.rio.gdprdoc:core")).using(project(":core"))
    }
}