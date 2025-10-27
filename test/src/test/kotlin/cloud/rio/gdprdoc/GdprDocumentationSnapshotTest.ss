╔═ gdpr documentation matches snapshot ═╗
# GDPR documentation for gdpr-doc-example

## Incoming

| Name | Source | What To Do | Fields  |
| --- | --- | --- | --- |
| [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#IN) | Kafka topic rio.iot-events | Enrich events during aggregation with driver card number | `driverCardNumber`, `name` |
| [IotDataKafka](#cloud.rio.example.adapter.kafka.IotDataKafka#IN) | Kafka topic rio.iot-events | Generate aggregated events | `assetId`, `timestamp`, `position`, `driverCardNumber` |
| [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#IN) | Kafka topic rio.permissions | Implement Access Control for API | `userId`, `hasAccess` |
| [DriverDTO](#cloud.rio.example.adapter.restclient.DriverDTO#IN) | Some external service | Forward via API | `id`, `identification`, `firstName`, `lastName`, `address.street` |

<details><summary>Field Details</summary>

<a id="cloud.rio.example.adapter.kafka.DriverKafka#IN"></a>

<h3>DriverKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>driverCardNumber</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>name</code></td>
      <td><span style="background-color:red; padding:2px 10px; border-radius:3px;">PII</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.kafka.IotDataKafka#IN"></a>

<h3>IotDataKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>assetId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>timestamp</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>Instant</td>
    </tr>
    <tr>
      <td><code>position</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>PositionKafka</td>
    </tr>
    <tr>
      <td><code>driverCardNumber</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.kafka.PermissionsKafka#IN"></a>

<h3>PermissionsKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>userId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>hasAccess</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>boolean</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.restclient.DriverDTO#IN"></a>

<h3>DriverDTO</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>id</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>UUID</td>
    </tr>
    <tr>
      <td><code>identification</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>firstName</code></td>
      <td><span style="background-color:red; padding:2px 10px; border-radius:3px;">PII</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>lastName</code></td>
      <td><span style="background-color:red; padding:2px 10px; border-radius:3px;">PII</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>address</code></td>
      <td>-</td>
      <td>AddressDTO</td>
    </tr>
    <tr>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;<code>address.street</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

</details>

## Persisted

| Name | Database identifier | Responsible For Deletion | Retention | Fields  |
| --- | --- | --- | --- | --- |
| [DriverEventDb](#cloud.rio.example.adapter.db.DriverEventDb#DB) | arn:aws:dynamodb:region:accountId:table/driver-events | Dev team | Kept for 30 days | `assetId`, `timestamp`, `position.latitude`, `position.longitude`, `driverCardNumber` |
| [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#DB) | arn:aws:dynamodb:region:accountId:table/driver-events | Owner of the upstream data source | Kept until data is deleted upstream | `driverCardNumber`, `name` |
| [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#DB) | arn:aws:dynamodb:region:accountId:table/permissions | Owner of the upstream data source | Kept until data is deleted upstream | `userId`, `hasAccess` |

<details><summary>Field Details</summary>

<a id="cloud.rio.example.adapter.db.DriverEventDb#DB"></a>

<h3>DriverEventDb</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>assetId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>timestamp</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>Instant</td>
    </tr>
    <tr>
      <td><code>position</code></td>
      <td>-</td>
      <td>List&lt;PositionDb&gt;</td>
    </tr>
    <tr>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;<code>position.latitude</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>double</td>
    </tr>
    <tr>
      <td>&nbsp;&nbsp;&nbsp;&nbsp;<code>position.longitude</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>double</td>
    </tr>
    <tr>
      <td><code>driverCardNumber</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.kafka.DriverKafka#DB"></a>

<h3>DriverKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>driverCardNumber</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>name</code></td>
      <td><span style="background-color:red; padding:2px 10px; border-radius:3px;">PII</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.kafka.PermissionsKafka#DB"></a>

<h3>PermissionsKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>userId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>hasAccess</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>boolean</td>
    </tr>
  </tbody>
</table>

</details>

## Outgoing

| Name | Shared With | Why | Fields  |
| --- | --- | --- | --- |
| [DriverEventKafka](#cloud.rio.example.adapter.publisher.DriverEventKafka#OUT) | Published to kafka topic rio.driver-events | To provide driver events to other services | `assetId`, `timestamp`, `driverCardNumber` |
| [DriverEventRest](#cloud.rio.example.adapter.rest.DriverEventRest#OUT) | Logged in user via frontend / API call | To show the driver event on the live monitor | `assetId`, `timestamp`, `address`, `driverName` |

<details><summary>Field Details</summary>

<a id="cloud.rio.example.adapter.publisher.DriverEventKafka#OUT"></a>

<h3>DriverEventKafka</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>assetId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>timestamp</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>Instant</td>
    </tr>
    <tr>
      <td><code>driverCardNumber</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

<a id="cloud.rio.example.adapter.rest.DriverEventRest#OUT"></a>

<h3>DriverEventRest</h3>
<table>
  <thead>
    <tr>
      <th>Field Name</th>
      <th>PII Level</th>
      <th>Type</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>assetId</code></td>
      <td><span style="background-color:darkorange; padding:2px 10px; border-radius:3px;">PSEUDONYM</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>timestamp</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>Instant</td>
    </tr>
    <tr>
      <td><code>address</code></td>
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>String</td>
    </tr>
    <tr>
      <td><code>driverName</code></td>
      <td><span style="background-color:red; padding:2px 10px; border-radius:3px;">PII</span></td>
      <td>String</td>
    </tr>
  </tbody>
</table>

</details>


╔═ [end of file] ═╗
