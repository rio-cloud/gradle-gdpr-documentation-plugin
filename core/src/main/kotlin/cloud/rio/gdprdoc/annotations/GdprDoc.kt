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

package cloud.rio.gdprdoc.annotations

import cloud.rio.gdprdoc.model.PiiLevel
import kotlin.reflect.KClass

const val READ_MODEL_DEFAULT_RETENTION = "Kept until data is deleted upstream"
const val READ_MODEL_DEFAULT_RESPONSIBLE_FOR_DELETION = "Owner of the upstream data source"


@Target(AnnotationTarget.CLASS)
annotation class GdprData {

    @GdprData
    @Target(AnnotationTarget.CLASS)
    annotation class Incoming(
        val whereFrom: String,
        val whatToDo: String,
        val links: Array<KClass<*>> = [],
    )

    @GdprData
    @Target(AnnotationTarget.CLASS)
    annotation class Outgoing(
        val sharedWith: String,
        val why: String,
        val links: Array<KClass<*>> = [],
    )

    @GdprData
    @Target(AnnotationTarget.CLASS)
    annotation class Persisted(
        val databaseIdentifier: String,
        val retention: String,
        val responsibleForDeletion: String,
        val links: Array<KClass<*>> = [],
    )

    @GdprData
    @Target(AnnotationTarget.CLASS)
    annotation class ReadModel(
        val whereFrom: String,
        val whatToDo: String,
        val databaseIdentifier: String,
        val retention: String = READ_MODEL_DEFAULT_RETENTION,
        val responsibleForDeletion: String = READ_MODEL_DEFAULT_RESPONSIBLE_FOR_DELETION,
        val links: Array<KClass<*>> = [],
    )

    @Target(AnnotationTarget.FIELD)
    annotation class Field(
        val level: PiiLevel,
    )
}
