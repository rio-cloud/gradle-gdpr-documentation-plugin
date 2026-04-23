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

package cloud.rio.example.adapter.rest

import cloud.rio.gdprdoc.annotations.GdprData
import cloud.rio.gdprdoc.model.PiiLevel

@GdprData.Outgoing(
    sharedWith = "Driver API",
    why = "Expose polymorphic driver profiles to the frontend",
)
data class DriverProfileRest(
    @GdprData.NestedType
    val driver: DriverResponse,
)

sealed interface DriverResponse

data class DriverOfTypeA(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val type: String = "type-a",
    @GdprData.NestedType
    val address: DriverAddressResource,
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val driverId: String,
) : DriverResponse

data class DriverOfTypeB(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val type: String = "type-b",
    @GdprData.NestedType
    val address: DriverAddressResource,
    @GdprData.Field(level = PiiLevel.NON_PII)
    val fleetOperator: String,
) : DriverResponse

data class DriverAddressResource(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val street: String,
)
