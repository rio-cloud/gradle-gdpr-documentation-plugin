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
import cloud.rio.gdprdoc.report.GdprReport
import cloud.rio.gdprdoc.report.MarkdownReporter
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.ScanResult
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
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

    private lateinit var scanResult: ScanResult

    @TaskAction
    fun process() {

        val gdprDataItems = mutableListOf<GdprDataItem>()
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

        this.scanResult = scanResult

        scanResult.getClassesWithAnnotation(GdprData::class.java.canonicalName)
            .filterNot { it.name.startsWith("cloud.rio.gdprdoc.annotations") }
            .forEach { classInfo ->
                try {
                    logger.debug("Processing class: ${classInfo.name}")
                    val newItems = processClass(classInfo)
                    gdprDataItems.addAll(newItems)
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
            val additionalGdprDataItems = additionalGdprDataMapper.mapToGdprDataItems(
                additionalGdprData
            )
            additionalGdprDataItems.forEach { newItem ->
                gdprDataItems.removeIf { existingItem -> existingItem.id == newItem.id }
                gdprDataItems.add(newItem)
            }
        }

        val report = GdprReport(serviceName = serviceName.get(), data = gdprDataItems)

        val formattedReport = MarkdownReporter().generateReport(report = report)
        val destination = markdownReport.get().asFile
        destination.writeText(formattedReport)

        logger.lifecycle("GDPR Data Items: $gdprDataItems")
    }

    fun processClass(classInfo: ClassInfo): List<GdprDataItem> {
        val items = mutableListOf<GdprDataItem>()

        classInfo.processAnnotation(GdprData.Incoming::class.java) { gdprData, fieldItems ->
            val id = GdprItemId(classInfo.name + "#IN")
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
            listOf(
                GdprDataItem.Persisted(
                    id = id,
                    name = classInfo.simpleName,
                    retention = gdprData.retention,
                    responsibleForDeletion = gdprData.responsibleForDeletion,
                    databaseIdentifier = gdprData.databaseIdentifier,
                    fields = fieldItems,
                ),
            )
        }.let { items.addAll(it) }

        classInfo.processAnnotation(GdprData.ReadModel::class.java) { gdprData, fieldItems ->
            val dbId = GdprItemId(classInfo.name + "#DB")
            val inId = GdprItemId(classInfo.name + "#IN")
            listOf(
                GdprDataItem.Persisted(
                    id = dbId,
                    name = classInfo.simpleName,
                    retention = gdprData.retention,
                    responsibleForDeletion = gdprData.responsibleForDeletion,
                    databaseIdentifier = gdprData.databaseIdentifier,
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

        return items
    }

    fun <T : Annotation> ClassInfo.processAnnotation(
        clazz: Class<out T>,
        process: (T, List<GdprDataItem.Field>) -> List<GdprDataItem>,
    ): List<GdprDataItem> {
        val fieldItems = collectAllFields(this, "", 0, mutableSetOf())
        return getAnnotationInfo(clazz)
            ?.loadClassAndInstantiate()
            ?.uncheckedCast<T>()
            ?.let { process(it, fieldItems) } ?: emptyList()
    }

    private fun collectAllFields(
        classInfo: ClassInfo,
        pathPrefix: String,
        depth: Int,
        visited: MutableSet<String>,
    ): List<GdprDataItem.Field> {
        if (classInfo.name in visited) {
            logger.warn("Skipping already visited class ${classInfo.name}")
            return emptyList()
        }
        if (depth > 10) {
            logger.warn("Maximum recursion depth reached for class ${classInfo.name}")
            return emptyList()
        }
        visited.add(classInfo.name)

        val fields = mutableListOf<GdprDataItem.Field>()

        classInfo.fieldInfo.forEach { fieldInfo ->
            val gdprFieldAnnotation = fieldInfo.getAnnotationInfo(GdprData.Field::class.java)
            val nestedTypeAnnotation = fieldInfo.getAnnotationInfo(GdprData.NestedType::class.java)

            if (gdprFieldAnnotation != null || nestedTypeAnnotation != null) {
                val fieldPath = if (pathPrefix.isEmpty()) fieldInfo.name else "$pathPrefix.${fieldInfo.name}"

                val piiLevel = if (nestedTypeAnnotation != null) {
                    null  // Complex types don't get PII levels
                } else {
                    gdprFieldAnnotation?.let {
                        (it.loadClassAndInstantiate() as GdprData.Field).level
                    }
                }

                fields.add(
                    GdprDataItem.Field(
                        name = fieldPath,
                        type = fieldInfo.typeSignatureOrTypeDescriptor.toStringWithSimpleNames(),
                        level = piiLevel,
                        depth = depth
                    )
                )
                logger.info(fields.joinToString(separator = "\n"))

                if (nestedTypeAnnotation != null) {
                    val typeSignature = fieldInfo.typeSignature as ClassRefTypeSignature
                    val referencedClassNames = typeSignature.typeArguments.map { it.toString() }
                    resolveFieldsFromNestedClass(referencedClassNames, fieldPath, depth, visited, fields)
                }
            }
        }

        return fields
    }

    private fun resolveFieldsFromNestedClass(
        referencedClassNames: List<String>,
        currentFieldPath: String,
        currentDepth: Int,
        alreadyVisitedClasses: MutableSet<String>,
        fields: MutableList<GdprDataItem.Field>,
    ) {
        referencedClassNames.forEach { className ->
            val fieldTypeClass = scanResult.getClassInfo(className)
            fieldTypeClass?.let { nestedClass ->
                val nestedFields = collectAllFields(
                    nestedClass,
                    currentFieldPath,
                    currentDepth + 1,
                    alreadyVisitedClasses.toMutableSet()
                )
                fields.addAll(nestedFields)
            }
        }
    }

    fun resolveJarPathForClass(className: String, classLoader: ClassLoader): File {
        val classFilePath = className.replace('.', '/') + ".class"
        val resourceUrl: URL = classLoader.getResource(classFilePath)!!

        return File(resourceUrl.path.substringBefore("!").removePrefix("file:"))
    }

}

