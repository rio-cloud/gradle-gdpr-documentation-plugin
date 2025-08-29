╔═ gdpr documentation matches snapshot ═╗
# GDPR documentation for gdpr-doc-example

## Data Flow Diagram

```plantuml
digraph G {
  rankdir=LR;
  nodesep=0.6;
    subgraph cluster_legend {
      label="Legend";
      color=darkgray;
      margin=10;
      key_in [label="Incoming", shape=box];
      key_db [label="Persisted", shape=cylinder];
      key_out [label="Outgoing", shape=hexagon];
      key_in -> key_db [label="data flow", style=solid];
      key_db -> key_out [label="data flow", style=solid];
      key_in -> key_out [label="related to", style=dotted, dir=none];
      key_in [style=filled, fillcolor=white];
      key_db [style=filled, fillcolor=white];
      key_out [style=filled, fillcolor=white];
    }
  subgraph cluster_main {
    label="";
    margin=40;
    color=white;
    "cloud.rio.example.adapter.db.DriverEventDb#DB" [label="DriverEventDb", shape=cylinder];
    "cloud.rio.example.adapter.kafka.DriverKafka#DB" [label="DriverKafka", shape=cylinder];
    "cloud.rio.example.adapter.kafka.DriverKafka#IN" [label="DriverKafka", shape=box];
    "cloud.rio.example.adapter.kafka.IotDataKafka#IN" [label="IotDataKafka", shape=box];
    "cloud.rio.example.adapter.kafka.PermissionsKafka#DB" [label="PermissionsKafka", shape=cylinder];
    "cloud.rio.example.adapter.kafka.PermissionsKafka#IN" [label="PermissionsKafka", shape=box];
    "cloud.rio.example.adapter.publisher.DriverEventKafka#OUT" [label="DriverEventKafka", shape=hexagon];
    "cloud.rio.example.adapter.rest.DriverEventRest#OUT" [label="DriverEventRest", shape=hexagon];
    { rank=same; "cloud.rio.example.adapter.kafka.DriverKafka#IN"; "cloud.rio.example.adapter.kafka.IotDataKafka#IN"; "cloud.rio.example.adapter.kafka.PermissionsKafka#IN"; }
    { rank=same; "cloud.rio.example.adapter.db.DriverEventDb#DB"; "cloud.rio.example.adapter.kafka.DriverKafka#DB"; "cloud.rio.example.adapter.kafka.PermissionsKafka#DB"; }
    { rank=same; "cloud.rio.example.adapter.publisher.DriverEventKafka#OUT"; "cloud.rio.example.adapter.rest.DriverEventRest#OUT"; }
    "cloud.rio.example.adapter.kafka.IotDataKafka#IN" -> "cloud.rio.example.adapter.db.DriverEventDb#DB";
    "cloud.rio.example.adapter.kafka.DriverKafka#IN" -> "cloud.rio.example.adapter.kafka.DriverKafka#DB";
    "cloud.rio.example.adapter.kafka.DriverKafka#DB" -> "cloud.rio.example.adapter.rest.DriverEventRest#OUT";
    "cloud.rio.example.adapter.kafka.PermissionsKafka#IN" -> "cloud.rio.example.adapter.kafka.PermissionsKafka#DB";
    "cloud.rio.example.adapter.kafka.IotDataKafka#IN" -> "cloud.rio.example.adapter.publisher.DriverEventKafka#OUT";
    "cloud.rio.example.adapter.publisher.DriverEventKafka#OUT" -> "cloud.rio.example.adapter.rest.DriverEventRest#OUT" [dir=none, style=dotted];
    "cloud.rio.example.adapter.db.DriverEventDb#DB" -> "cloud.rio.example.adapter.rest.DriverEventRest#OUT";
  }
}
```
## Incoming

| Name | Source | What To Do | Fields | Links |
| --- | --- | --- | --- | ----- |
| [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#IN) | Kafka topic rio.iot-events | Enrich events during aggregation with driver card number | `driverCardNumber`, `name` | [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#DB) |
| [IotDataKafka](#cloud.rio.example.adapter.kafka.IotDataKafka#IN) | Kafka topic rio.iot-events | Generate aggregated events | `assetId`, `timestamp`, `position`, `driverCardNumber` | [DriverEventDb](#cloud.rio.example.adapter.db.DriverEventDb#DB), [DriverEventKafka](#cloud.rio.example.adapter.publisher.DriverEventKafka#OUT) |
| [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#IN) | Kafka topic rio.permissions | Implement Access Control for API | `userId`, `hasAccess` | [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#DB) |

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

</details>

## Persisted

| Name | Responsible For Deletion | Retention | Fields | Links |
| --- | --- | --- | --- | ----- |
| [DriverEventDb](#cloud.rio.example.adapter.db.DriverEventDb#DB) | Dev team | Kept for 30 days | `assetId`, `timestamp`, `position`, `driverCardNumber` | [IotDataKafka](#cloud.rio.example.adapter.kafka.IotDataKafka#IN), [DriverEventRest](#cloud.rio.example.adapter.rest.DriverEventRest#OUT) |
| [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#DB) | Owner of the upstream data source | Kept until data is deleted upstream | `driverCardNumber`, `name` | [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#IN), [DriverEventRest](#cloud.rio.example.adapter.rest.DriverEventRest#OUT) |
| [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#DB) | Owner of the upstream data source | Kept until data is deleted upstream | `userId`, `hasAccess` | [PermissionsKafka](#cloud.rio.example.adapter.kafka.PermissionsKafka#IN) |

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
      <td><span style="background-color:green; padding:2px 10px; border-radius:3px;">NON PII</span></td>
      <td>PositionDb</td>
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

| Name | Shared With | Why | Fields | Links |
| --- | --- | --- | --- | ----- |
| [DriverEventKafka](#cloud.rio.example.adapter.publisher.DriverEventKafka#OUT) | Published to kafka topic rio.driver-events | To provide driver events to other services | `assetId`, `timestamp`, `driverCardNumber` | [IotDataKafka](#cloud.rio.example.adapter.kafka.IotDataKafka#IN), [DriverEventRest](#cloud.rio.example.adapter.rest.DriverEventRest#OUT) |
| [DriverEventRest](#cloud.rio.example.adapter.rest.DriverEventRest#OUT) | Logged in user via frontend / API call | To show the driver event on the live monitor | `assetId`, `timestamp`, `address`, `driverName` | [DriverKafka](#cloud.rio.example.adapter.kafka.DriverKafka#DB), [DriverEventKafka](#cloud.rio.example.adapter.publisher.DriverEventKafka#OUT), [DriverEventDb](#cloud.rio.example.adapter.db.DriverEventDb#DB) |

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
