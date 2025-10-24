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

package cloud.rio.example.adapter.kafka

import cloud.rio.example.adapter.rest.DriverEventRest
import cloud.rio.gdprdoc.annotations.GdprData
import cloud.rio.gdprdoc.model.PiiLevel
import java.time.Instant

@GdprData.Incoming(
    whereFrom = "Kafka topic rio.iot-events",
    whatToDo = "Generate aggregated events",
)
data class IotDataKafka(
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val assetId: String,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val timestamp: Instant,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val position: PositionKafka,
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val driverCardNumber: String,
)

data class PositionKafka(
    val latitude: Double,
    val longitude: Double,
)

@GdprData.ReadModel(
    whereFrom = "Kafka topic rio.iot-events",
    whatToDo = "Enrich events during aggregation with driver card number",
    databaseIdentifier = "arn:aws:dynamodb:region:accountId:table/driver-events",
)
data class DriverKafka(
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val driverCardNumber: String,
    @GdprData.Field(level = PiiLevel.PII)
    val name: String,
)

@GdprData.ReadModel(
    whereFrom = "Kafka topic rio.permissions",
    whatToDo = "Implement Access Control for API",
    databaseIdentifier = "arn:aws:dynamodb:region:accountId:table/permissions",
)
data class PermissionsKafka(
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val userId: String,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val hasAccess: Boolean,
)
