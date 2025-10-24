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
import kotlin.reflect.KProperty1


class MarkdownReporter {


    fun generateReport(report: GdprReport): String {
        val markdownBuilder = StringBuilder()

        markdownBuilder.append("# GDPR documentation for ${report.serviceName}\n\n")

        mainTable(markdownBuilder, report.data)

        return markdownBuilder.toString()
    }

    private fun mainTable(
        markdownBuilder: StringBuilder,
        items: List<GdprDataItem>,
        compactView: Boolean = false,
    ) {
        val incomingItems = items.filterIsInstance<GdprDataItem.Incoming>()
        val outgoingItems = items.filterIsInstance<GdprDataItem.Outgoing>()
        val persistedItems = items.filterIsInstance<GdprDataItem.Persisted>()

        if (incomingItems.isNotEmpty()) {
            if (!compactView) {
                markdownBuilder.append("## Incoming\n\n")
            }
            renderIncomingTable(markdownBuilder, incomingItems, compactView)
            renderFieldDetails(markdownBuilder, incomingItems)
        }

        if (persistedItems.isNotEmpty()) {
            if (!compactView) {
                markdownBuilder.append("## Persisted\n\n")
            }
            renderPersistedTable(markdownBuilder, persistedItems, compactView)
            renderFieldDetails(markdownBuilder, persistedItems)
        }

        if (outgoingItems.isNotEmpty()) {
            if (!compactView) {
                markdownBuilder.append("## Outgoing\n\n")
            }
            renderOutgoingTable(markdownBuilder, outgoingItems, compactView)
            renderFieldDetails(markdownBuilder, outgoingItems)
        }
    }

    private fun renderFieldDetails(markdownBuilder: StringBuilder, items: List<GdprDataItem>) {
        markdownBuilder.append("<details>")
        markdownBuilder.append("<summary>Field Details</summary>\n\n")
        items.forEach { item ->
            markdownBuilder.append("<a id=\"${item.id}\"></a>\n\n")
            markdownBuilder.append("<h3>${item.name}</h3>\n")
            // Begin HTML table
            markdownBuilder.append("<table>\n")
            markdownBuilder.append("  <thead>\n")
            markdownBuilder.append("    <tr>\n")
            markdownBuilder.append("      <th>Field Name</th>\n")
            markdownBuilder.append("      <th>PII Level</th>\n")
            markdownBuilder.append("      <th>Type</th>\n")
            markdownBuilder.append("    </tr>\n")
            markdownBuilder.append("  </thead>\n")
            markdownBuilder.append("  <tbody>\n")
            item.fields.forEach { field ->
                markdownBuilder.append("    <tr>\n")
                markdownBuilder.append("      <td>${field.name.monoHtml()}</td>\n")
                markdownBuilder.append("      <td>${field.level.format()}</td>\n")
                markdownBuilder.append("      <td>${field.type}</td>\n")
                markdownBuilder.append("    </tr>\n")
            }
            markdownBuilder.append("  </tbody>\n")
            markdownBuilder.append("</table>\n\n")
            // End HTML table
        }
        markdownBuilder.append("</details>\n\n")
    }

    private fun <T : GdprDataItem> renderTable(
        markdownBuilder: StringBuilder,
        items: List<T>,
        fields: List<KProperty1<T, *>>,
    ) {
        markdownBuilder.append("| ${fields.joinToString(" | ") { headings[it]!! }}  |\n")
        markdownBuilder.append("| ${fields.joinToString(" | ") { "---" }} |\n")
        items.forEach { item ->
            val formattedRow = fields.joinToString(" | ") { field ->
                this.format(field, item)
            }
            markdownBuilder.append("| $formattedRow |\n")
        }
        markdownBuilder.append("\n")
    }

    private fun renderIncomingTable(
        markdownBuilder: StringBuilder,
        items: List<GdprDataItem.Incoming>,
        compactView: Boolean,
    ) {
        val fields = listOfNotNull(
            GdprDataItem.Incoming::name.takeIf { !compactView },
            GdprDataItem.Incoming::whereFrom,
            GdprDataItem.Incoming::whatToDo,
            GdprDataItem.Incoming::fields.takeIf { !compactView },
        )
        return renderTable(markdownBuilder, items, fields)
    }

    private fun renderOutgoingTable(
        markdownBuilder: StringBuilder,
        items: List<GdprDataItem.Outgoing>,
        compactView: Boolean,
    ) {
        val fields = listOfNotNull(
            GdprDataItem.Outgoing::name.takeIf { !compactView },
            GdprDataItem.Outgoing::sharedWith,
            GdprDataItem.Outgoing::why,
            GdprDataItem.Outgoing::fields.takeIf { !compactView },
        )
        return renderTable(markdownBuilder, items, fields)
    }

    private fun renderPersistedTable(
        markdownBuilder: StringBuilder,
        items: List<GdprDataItem.Persisted>,
        compactView: Boolean,
    ) {
        val fields = listOfNotNull(
            GdprDataItem.Persisted::name.takeIf { !compactView },
            GdprDataItem.Persisted::databaseIdentifier,
            GdprDataItem.Persisted::responsibleForDeletion,
            GdprDataItem.Persisted::retention,
            GdprDataItem.Persisted::fields.takeIf { !compactView },
        )
        return renderTable(markdownBuilder, items, fields)
    }


    private val headings: Map<KProperty1<*, *>, String> = mapOf(
        GdprDataItem.Incoming::name to "Name",
        GdprDataItem.Incoming::whereFrom to "Source",
        GdprDataItem.Incoming::whatToDo to "What To Do",
        GdprDataItem.Incoming::fields to "Fields",
        GdprDataItem.Outgoing::name to "Name",
        GdprDataItem.Outgoing::sharedWith to "Shared With",
        GdprDataItem.Outgoing::why to "Why",
        GdprDataItem.Outgoing::fields to "Fields",
        GdprDataItem.Persisted::name to "Name",
        GdprDataItem.Persisted::responsibleForDeletion to "Responsible For Deletion",
        GdprDataItem.Persisted::retention to "Retention",
        GdprDataItem.Persisted::fields to "Fields",
        GdprDataItem.Persisted::databaseIdentifier to "Database identifier",
    )

    private fun <T : GdprDataItem> format(field: KProperty1<T, *>, item: T) = when (field) {
        GdprDataItem.Incoming::name,
        GdprDataItem.Outgoing::name,
        GdprDataItem.Persisted::name,
            -> formatAsAnchorLink(item.name, item.id)

        GdprDataItem.Incoming::fields,
        GdprDataItem.Outgoing::fields,
        GdprDataItem.Persisted::fields,
            -> item.fields.joinToString(", ") { it.name.mono() }

        else -> item.let { field.get(it) }.toString()
    }

    private fun String.mono(): String = "`$this`"
    private fun String.monoHtml(): String = "<code>$this</code>"

    private fun PiiLevel.format(): String {
        val color = when (this) {
            PiiLevel.PII -> "red"
            PiiLevel.PSEUDONYM -> "darkorange"
            PiiLevel.NON_PII -> "green"
        }
        val displayName = this.name.replace("_", " ")
        return "<span style=\"background-color:$color; padding:2px 10px; border-radius:3px;\">${displayName}</span>"
    }

    private fun formatAsAnchorLink(name: String, id: GdprItemId): String = "[${name}](#${id.value.replace("$", "\\$")})"
}

