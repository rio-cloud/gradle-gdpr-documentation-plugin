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

package cloud.rio.gdprdoc

import cloud.rio.gdprdoc.additionalgdprdata.AdditionalGdprDataLoader
import cloud.rio.gdprdoc.additionalgdprdata.AdditionalGdprDataMapper
import cloud.rio.gdprdoc.annotations.GdprData
import cloud.rio.gdprdoc.report.GdprDataItem
import cloud.rio.gdprdoc.report.GdprItemId
import cloud.rio.gdprdoc.report.GdprItemRelation
import cloud.rio.gdprdoc.report.GdprReport
import cloud.rio.gdprdoc.report.MarkdownReporter
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.extensions.stdlib.uncheckedCast
import java.io.File
import java.net.URL
import java.net.URLClassLoader


abstract class GenerateGdprDocumentationTask : DefaultTask() {

    @get:InputFiles
    @get:Classpath
    abstract val classpath: ConfigurableFileCollection

    @get:Input
    abstract val serviceName: Property<String>

    @get:OutputFile
    abstract val markdownReport: RegularFileProperty

    @get:InputFiles
    @get:Optional
    abstract val additionalGdprDataFiles: ConfigurableFileCollection

    @TaskAction
    fun process() {

        val gdprDataItems = mutableListOf<GdprDataItem>()
        val linkTargetClassesByItemId = mutableMapOf<GdprItemId, MutableSet<String>>()

        classpath.files.forEach { file ->
            if (file.isDirectory) {
                logger.debug("Directory: ${file.absolutePath}")
            } else {
                logger.debug("File: ${file.absolutePath}")
            }
        }

        // Create a class loader with the project classpath and the current class loader, so the annotation classes are present
        // We cannot rely on the builtin classgraph classloader, because the cast to GdprData / GdprField fails in that case
        val classPathFiles =
            classpath.files + listOf(resolveJarPathForClass(GdprData::class.java.canonicalName, javaClass.classLoader))
        val scanClassLoader =
            URLClassLoader(classPathFiles.map { it.toURI().toURL() }.toTypedArray(), javaClass.classLoader)

        val scanResult = ClassGraph()
            .enableAllInfo() // not sure what exactly is needed, but this works for now
            .ignoreParentClassLoaders()
            .overrideClassLoaders(scanClassLoader)
            .scan()

        scanResult.getClassesWithAnnotation(GdprData::class.java.canonicalName).forEach { classInfo ->
            try {
                logger.debug("Processing class: ${classInfo.name}")
                val (newItems, newlinks) = processClass(classInfo)
                gdprDataItems.addAll(newItems)
                linkTargetClassesByItemId.putAll(newlinks)
            } catch (e: Exception) {
                logger.error("Error processing class ${classInfo.name}: ${e.message}")
                throw e
            }
        }

        val additionalGdprDataLoader = AdditionalGdprDataLoader()
        val additionalGdprDataMapper = AdditionalGdprDataMapper(classPathFiles, logger)
        additionalGdprDataFiles.files.forEach {
            logger.lifecycle("Loading additional GDPR data from file: ${it.absolutePath}")
            val additionalGdprData = try {
                additionalGdprDataLoader.loadAdditionalGdprDataFromYamlFile(it)
            } catch (e: Exception) {
                logger.warn("Cannot read additional GDPR data from file ${it.absolutePath}: ${e.message}")
                return@forEach
            }
            val (additionalGdprDataItems, additionalLinks) = additionalGdprDataMapper.mapToGdprDataItems(
                additionalGdprData
            )
            additionalGdprDataItems.forEach { newItem ->
                gdprDataItems.removeIf { existingItem -> existingItem.id == newItem.id }
                gdprDataItems.add(newItem)
            }
            linkTargetClassesByItemId.putAll(additionalLinks)
        }

        val gdprItemLinks = linkTargetClassesByItemId.flatMap { (source, targetClasses) ->
            targetClasses.flatMap { createLinks(sourceId = source, targetClassName = it, items = gdprDataItems) }
        }.toSet()

        val report = GdprReport(serviceName = serviceName.get(), data = gdprDataItems, relations = gdprItemLinks)

        val formattedReport = MarkdownReporter().generateReport(report = report)
        val destination = markdownReport.get().asFile
        destination.writeText(formattedReport)

        logger.lifecycle("GDPR Data Items: $gdprDataItems")
        logger.lifecycle("GDPR Links:\n${gdprItemLinks.joinToString("\n")}")
    }

    fun createLinks(sourceId: GdprItemId, targetClassName: String, items: List<GdprDataItem>): Set<GdprItemRelation> {
        val targetItems = items.filter { it.id.value.startsWith(targetClassName) }
        val sourceItem = items.find { it.id == sourceId } ?: return emptySet()

        val links = mutableSetOf<GdprItemRelation>()

        for (target in targetItems) {
            when {
                sourceItem is GdprDataItem.Incoming && target is GdprDataItem.Persisted -> {
                    links.add(GdprItemRelation.flow(sourceId, target.id))
                }

                sourceItem is GdprDataItem.Persisted && target is GdprDataItem.Outgoing -> {
                    links.add(GdprItemRelation.flow(sourceId, target.id))
                }

                sourceItem is GdprDataItem.Incoming && target is GdprDataItem.Outgoing -> {
                    links.add(GdprItemRelation.flow(sourceId, target.id))
                }

                sourceItem is GdprDataItem.Persisted && target is GdprDataItem.Incoming -> {
                    links.add(GdprItemRelation.flow(target.id, sourceId))
                }

                sourceItem is GdprDataItem.Outgoing && target is GdprDataItem.Persisted -> {
                    links.add(GdprItemRelation.flow(target.id, sourceId))
                }

                sourceItem is GdprDataItem.Outgoing && target is GdprDataItem.Incoming -> {
                    links.add(GdprItemRelation.flow(target.id, sourceId))
                }
                // Related links (same type, but not self)
                sourceItem::class == target::class && sourceId != target.id -> {
                    links.add(GdprItemRelation.relatedTo(sourceId, target.id))
                }
            }
        }

        return links
    }

    fun processClass(classInfo: ClassInfo): Pair<List<GdprDataItem>, Map<GdprItemId, MutableSet<String>>> {
        val items = mutableListOf<GdprDataItem>()

        val linkTargetClassesByItemId = mutableMapOf<GdprItemId, MutableSet<String>>()

        classInfo.processAnnotation(GdprData.Incoming::class.java) { gdprData, fieldItems ->
            val id = GdprItemId(classInfo.name + "#IN")
            linkTargetClassesByItemId.getOrPut(id) { mutableSetOf() }
                .addAll(gdprData.links.map { it.java.name })
            listOf(
                GdprDataItem.Incoming(
                    id = id,
                    name = classInfo.simpleName,
                    whereFrom = gdprData.whereFrom,
                    whatToDo = gdprData.whatToDo,
                    fields = fieldItems,
                ),
            )
        }.let { items.addAll(it) }


        classInfo.processAnnotation(GdprData.Outgoing::class.java) { gdprData, fieldItems ->
            val id = GdprItemId(classInfo.name + "#OUT")
            linkTargetClassesByItemId.getOrPut(id) { mutableSetOf() }
                .addAll(gdprData.links.map { it.java.name })
            listOf(
                GdprDataItem.Outgoing(
                    id = id,
                    name = classInfo.simpleName,
                    sharedWith = gdprData.sharedWith,
                    why = gdprData.why,
                    fields = fieldItems,
                ),
            )
        }.let { items.addAll(it) }

        classInfo.processAnnotation(GdprData.Persisted::class.java) { gdprData, fieldItems ->
            val id = GdprItemId(classInfo.name + "#DB")
            linkTargetClassesByItemId.getOrPut(id) { mutableSetOf() }
                .addAll(gdprData.links.map { it.java.name })
            listOf(
                GdprDataItem.Persisted(
                    id = id,
                    name = classInfo.simpleName,
                    retention = gdprData.retention,
                    responsibleForDeletion = gdprData.responsibleForDeletion,
                    fields = fieldItems,
                ),
            )
        }.let { items.addAll(it) }

        classInfo.processAnnotation(GdprData.ReadModel::class.java) { gdprData, fieldItems ->
            val dbId = GdprItemId(classInfo.name + "#DB")
            val inId = GdprItemId(classInfo.name + "#IN")
            linkTargetClassesByItemId.getOrPut(inId) { mutableSetOf() }
                .add(classInfo.name)
            linkTargetClassesByItemId.getOrPut(dbId) { mutableSetOf() }
                .addAll(gdprData.links.map { it.java.name })
            listOf(
                GdprDataItem.Persisted(
                    id = dbId,
                    name = classInfo.simpleName,
                    retention = gdprData.retention,
                    responsibleForDeletion = gdprData.responsibleForDeletion,
                    fields = fieldItems,
                ),
                GdprDataItem.Incoming(
                    id = inId,
                    name = classInfo.simpleName,
                    whereFrom = gdprData.whereFrom,
                    whatToDo = gdprData.whatToDo,
                    fields = fieldItems,
                ),
            )
        }.let { items.addAll(it) }

        if (items.isEmpty()) {
            logger.warn("No annotation found for class ${classInfo.name} other than the @GdprData marker")
        }

        return Pair(items, linkTargetClassesByItemId)
    }

    fun <T : Annotation> ClassInfo.processAnnotation(
        clazz: Class<out T>,
        process: (T, List<GdprDataItem.Field>) -> List<GdprDataItem>,
    ): List<GdprDataItem> {
        val fieldItems: List<GdprDataItem.Field> = fieldInfo
            .filter { it.hasAnnotation(GdprData.Field::class.java) }
            .map {
                val gdprField =
                    it.getAnnotationInfo(GdprData.Field::class.java).loadClassAndInstantiate() as GdprData.Field
                GdprDataItem.Field(
                    name = it.name,
                    type = it.typeSignatureOrTypeDescriptor.toStringWithSimpleNames(),
                    level = gdprField.level,
                )
            }
        return getAnnotationInfo(clazz)
            ?.loadClassAndInstantiate()
            ?.uncheckedCast<T>()
            ?.let { process(it, fieldItems) } ?: emptyList()
    }

    fun resolveJarPathForClass(className: String, classLoader: ClassLoader): File {
        val classFilePath = className.replace('.', '/') + ".class"
        val resourceUrl: URL = classLoader.getResource(classFilePath)!!

        return File(resourceUrl.path.substringBefore("!").removePrefix("file:"))
    }

}

