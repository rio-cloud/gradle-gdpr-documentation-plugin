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
            val fields = collectFields(additionalData, item, clazz, 0, "", mutableSetOf())

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

    private fun collectFields(
        additionalData: AdditionalGdprData,
        currentItem: AdditionalGdprDataItem,
        clazz: kotlin.reflect.KClass<*>,
        depth: Int,
        pathPrefix: String,
        visited: MutableSet<String>,
    ): List<GdprDataItem.Field> {
        if (depth > 10) {
            logger.warn("Maximum nesting depth reached for class {}", clazz.qualifiedName)
            return emptyList()
        }

        val className = clazz.qualifiedName ?: clazz.simpleName ?: "Unknown"
        if (visited.contains(className)) {
            logger.warn("Circular reference detected for class {}, skipping", className)
            return emptyList()
        }
        visited.add(className)

        val fields = mutableListOf<GdprDataItem.Field>()

        for (fieldData in currentItem.fields) {
            val field = Field.fromFieldData(fieldData)
            val member = clazz.memberProperties.find { it.name == field.name }

            if (member == null) {
                logger.warn("Field not found: {} in class {}, skipping", field.name, clazz.qualifiedName)
                continue
            }

            val fieldType = member.returnType.jvmErasure.simpleName ?: "Unknown"
            val fieldPath = if (pathPrefix.isEmpty()) field.name else "$pathPrefix.${field.name}"

            when (field) {
                is Field.SimpleType -> {
                    fields.add(
                        GdprDataItem.Field(
                            name = fieldPath,
                            level = field.level,
                            type = fieldType,
                            depth = depth
                        )
                    )
                }

                is Field.NestedType -> {
                    fields.add(
                        GdprDataItem.Field(
                            name = fieldPath,
                            level = null,
                            type = fieldType,
                            depth = depth
                        )
                    )

                    for (nestedClassName in field.nestedTypeClasses) {
                        if (visited.contains(nestedClassName)) {
                            logger.warn("Circular reference detected for nested class {}, skipping", nestedClassName)
                            continue
                        }

                        val nestedClass = Class.forName(nestedClassName, true, classLoader).kotlin
                        val nestedClassConfig = additionalData.classes.find { it.className == nestedClassName }

                        if (nestedClassConfig != null) {
                            // Use YAML configuration for nested class
                            val nestedFields = collectFields(
                                additionalData,
                                nestedClassConfig,
                                nestedClass,
                                depth + 1,
                                fieldPath,
                                visited.toMutableSet()
                            )
                            fields.addAll(nestedFields)
                        }
                    }
                }
            }
        }

        visited.remove(className)
        return fields
    }
}
