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
    ): List<GdprDataItem> {
        val gdprDataItems = mutableListOf<GdprDataItem>()

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
            }
        }

        return gdprDataItems
    }
}
