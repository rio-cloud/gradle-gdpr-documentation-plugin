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

import com.diffplug.selfie.Selfie.expectSelfie
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class GdprDocumentationSnapshotTest {

    @Test
    fun `gdpr documentation matches snapshot`() {
        val path = Paths.get("build/reports/gdpr-documentation.md")
        val content = Files.readString(path)
        expectSelfie(content).toMatchDisk()
    }

    @Test
    fun `sealed nested polymorphic types are expanded`() {
        val content = Files.readString(Paths.get("build/reports/gdpr-documentation.md"))

        assertAll(
            { assertTrue(content.contains("DriverProfileRest")) },
            { assertTrue(content.contains("`driver.type`")) },
            { assertTrue(content.contains("`driver.address.street`")) },
            { assertTrue(content.contains("`driver.driverId`")) },
            { assertTrue(content.contains("`driver.fleetOperator`")) },
            { assertEquals(2, Regex("driver\\.type").findAll(content).count()) },
        )
    }

    @Test
    fun `abstract nested polymorphic collection types are expanded`() {
        val content = Files.readString(Paths.get("build/reports/gdpr-documentation.md"))

        assertAll(
            { assertTrue(content.contains("FleetMovementRest")) },
            { assertTrue(content.contains("`movements.kind`")) },
            { assertTrue(content.contains("`movements.driverId`")) },
            { assertTrue(content.contains("`movements.depot.city`")) },
            { assertTrue(content.contains("`movements.routeNumber`")) },
            { assertEquals(2, Regex("movements\\.kind").findAll(content).count()) },
        )
    }

}
