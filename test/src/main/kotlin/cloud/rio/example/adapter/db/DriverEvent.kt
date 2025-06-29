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

package cloud.rio.example.adapter.db

import cloud.rio.example.adapter.kafka.IotDataKafka
import cloud.rio.gdprdoc.annotations.GdprData
import cloud.rio.gdprdoc.model.PiiLevel
import java.time.Instant

@GdprData.Persisted(
    retention = "Kept for 30 days",
    responsibleForDeletion = "Dev team",
    links = [IotDataKafka::class],
)
data class DriverEventDb(
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val assetId: String,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val timestamp: Instant,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val position: PositionDb,
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val driverCardNumber: String,
)

data class PositionDb(
    val latitude: Double,
    val longitude: Double,
)
