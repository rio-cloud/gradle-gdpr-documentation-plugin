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

tasks.register("clean") {
    dependsOn(gradle.includedBuilds.map { it.task(":clean") })
}

tasks.register("build") {
    dependsOn(gradle.includedBuilds.map { it.task(":build") })
}

tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuilds.filter { it.name != "test" }.map { it.task(":publishToMavenLocal") })
}
