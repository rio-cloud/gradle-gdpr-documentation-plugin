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

package cloud.rio.gdprdoc.report

import cloud.rio.gdprdoc.model.PiiLevel

@JvmInline
value class GdprItemId(val value: String): Comparable<GdprItemId> {
    override fun toString(): String = value
    override fun compareTo(other: GdprItemId): Int = value.compareTo(other.value)
}

data class GdprReport(
    val serviceName: String,
    val data: List<GdprDataItem>,
) {
    fun get(id: GdprItemId): GdprDataItem? {
        return data.find { it.id == id }
    }
}

sealed class GdprDataItem {
    abstract val id: GdprItemId
    abstract val name: String
    abstract val fields: List<Field>

    data class Incoming(
        override val id: GdprItemId,
        override val name: String,
        val whereFrom: String,
        val whatToDo: String,
        override val fields: List<Field> = emptyList(),
    ) : GdprDataItem()

    data class Outgoing(
        override val id: GdprItemId,
        override val name: String,
        val sharedWith: String,
        val why: String,
        override val fields: List<Field> = emptyList(),
    ) : GdprDataItem()

    data class Persisted(
        override val id: GdprItemId,
        override val name: String,
        val databaseIdentifier: String,
        val retention: String,
        val responsibleForDeletion: String,
        override val fields: List<Field> = emptyList(),
    ) : GdprDataItem()

    data class Field(
        val name: String,
        val type: String,
        val level: PiiLevel,
    )
}
