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

package cloud.rio.gdprdoc.additionalgdprdata

import cloud.rio.gdprdoc.annotations.READ_MODEL_DEFAULT_RESPONSIBLE_FOR_DELETION
import cloud.rio.gdprdoc.annotations.READ_MODEL_DEFAULT_RETENTION
import cloud.rio.gdprdoc.model.PiiLevel
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalGdprData(
    val classes: List<AdditionalGdprDataItem>,
)

@Serializable
data class AdditionalGdprDataItem(
    val className: String,
    val outgoing: Outgoing? = null,
    val incoming: Incoming? = null,
    val persisted: Persisted? = null,
    val readModel: ReadModel? = null,
    val fields: List<Field>,
)

@Serializable
data class Outgoing(
    val sharedWith: String,
    val why: String,
)

@Serializable
data class Incoming(
    val whereFrom: String,
    val whatToDo: String,
)

@Serializable
data class Persisted(
    val databaseIdentifier: String,
    val retention: String,
    val responsibleForDeletion: String,
)

@Serializable
data class ReadModel(
    val whereFrom: String,
    val whatToDo: String,
    val databaseIdentifier: String,
    val retention: String = READ_MODEL_DEFAULT_RETENTION,
    val responsibleForDeletion: String = READ_MODEL_DEFAULT_RESPONSIBLE_FOR_DELETION,
)

@Serializable
data class Field(
    val name: String,
    val level: PiiLevel,
)
