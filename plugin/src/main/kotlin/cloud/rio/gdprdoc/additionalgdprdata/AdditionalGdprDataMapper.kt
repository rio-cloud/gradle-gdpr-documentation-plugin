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

import cloud.rio.gdprdoc.report.GdprDataItem
import cloud.rio.gdprdoc.report.GdprItemId
import org.gradle.api.logging.Logger
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure


class AdditionalGdprDataMapper(classPathFiles: Set<File>, val logger: Logger) {
    private val classLoader =
        URLClassLoader(classPathFiles.map { it.toURI().toURL() }.toTypedArray(), javaClass.classLoader)

    fun mapToGdprDataItems(
        additionalData: AdditionalGdprData,
    ): Pair<List<GdprDataItem>, Map<GdprItemId, MutableSet<String>>> {
        val gdprDataItems = mutableListOf<GdprDataItem>()
        val linkTargetClassesByItemId = mutableMapOf<GdprItemId, MutableSet<String>>()

        for (item in additionalData.classes) {
            val clazz = try {
                Class.forName(item.className, true, classLoader).kotlin
            } catch (_: ClassNotFoundException) {
                logger.warn("Class not found: {}, skipping", item.className)
                continue
            }

            val simpleClassName = clazz.simpleName ?: item.className

            val fields = item.fields.mapNotNull { field ->
                clazz.memberProperties.find { it.name == field.name }?.let { member ->
                    GdprDataItem.Field(
                        name = field.name,
                        level = field.level,
                        type = member.returnType.jvmErasure.simpleName ?: "Unknown"
                    )
                } ?: run {
                    logger.warn("Field not found: {} in class {}, skipping", field.name, item.className)
                    null
                }
            }

            item.outgoing?.let {
                val outgoing = GdprDataItem.Outgoing(
                    id = GdprItemId(item.className + "#OUT"),
                    name = simpleClassName,
                    sharedWith = it.sharedWith,
                    why = it.why,
                    fields = fields,
                )
                gdprDataItems.add(outgoing)
                linkTargetClassesByItemId.getOrPut(outgoing.id) { mutableSetOf() }
                    .addAll(it.links.filter(this::canLinkTo))
            }

            item.incoming?.let {
                val incoming = GdprDataItem.Incoming(
                    id = GdprItemId(item.className + "#IN"),
                    name = simpleClassName,
                    whereFrom = it.whereFrom,
                    whatToDo = it.whatToDo,
                    fields = fields,
                )
                gdprDataItems.add(incoming)
                linkTargetClassesByItemId.getOrPut(incoming.id) { mutableSetOf() }
                    .addAll(it.links.filter(this::canLinkTo))
            }

            item.persisted?.let {
                val persisted = GdprDataItem.Persisted(
                    id = GdprItemId(item.className + "#DB"),
                    name = simpleClassName,
                    retention = it.retention,
                    responsibleForDeletion = it.responsibleForDeletion,
                    databaseIdentifier = it.databaseIdentifier,
                    fields = fields,
                )
                gdprDataItems.add(persisted)
                linkTargetClassesByItemId.getOrPut(persisted.id) { mutableSetOf() }
                    .addAll(it.links.filter(this::canLinkTo))
            }

            item.readModel?.let {
                val persisted = GdprDataItem.Persisted(
                    id = GdprItemId(item.className + "#DB"),
                    name = simpleClassName,
                    retention = it.retention,
                    responsibleForDeletion = it.responsibleForDeletion,
                    databaseIdentifier = it.databaseIdentifier,
                    fields = fields,
                )
                val incoming = GdprDataItem.Incoming(
                    id = GdprItemId(item.className + "#IN"),
                    name = simpleClassName,
                    whereFrom = it.whereFrom,
                    whatToDo = it.whatToDo,
                    fields = fields,
                )
                gdprDataItems.add(persisted)
                gdprDataItems.add(incoming)
                linkTargetClassesByItemId.getOrPut(incoming.id) { mutableSetOf() }
                    .add(item.className)
                linkTargetClassesByItemId.getOrPut(persisted.id) { mutableSetOf() }
                    .addAll(it.links.filter(this::canLinkTo))
            }
        }

        return Pair(gdprDataItems, linkTargetClassesByItemId)
    }

    private fun canLinkTo(className: String): Boolean {
        try {
            Class.forName(className, true, classLoader)
        } catch (_: ClassNotFoundException) {
            logger.warn("Cannot create link to {} because class was not found, skipping", className)
            return false
        }
        return true
    }
}
