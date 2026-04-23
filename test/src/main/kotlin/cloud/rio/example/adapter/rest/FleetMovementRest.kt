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
    sharedWith = "Fleet monitor UI",
    why = "Expose polymorphic fleet movements to dispatchers",
)
data class FleetMovementRest(
    @GdprData.NestedType
    val movements: List<FleetMovement>,
)

abstract class FleetMovement

data class StopMovement(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val kind: String = "stop",
    @GdprData.Field(level = PiiLevel.PSEUDONYM)
    val driverId: String,
    @GdprData.NestedType
    val depot: DepotResource,
) : FleetMovement()

data class TransitMovement(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val kind: String = "transit",
    @GdprData.Field(level = PiiLevel.NON_PII)
    val routeNumber: String,
) : FleetMovement()

data class DepotResource(
    @GdprData.Field(level = PiiLevel.NON_PII)
    val city: String,
)
