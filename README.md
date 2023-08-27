# stream-metrics

Stream metrics enable to define metrics on stream(Kafka) using Sql. </br>
It has three components:

- stream : represents continuous unbounded flow of data into a kafka topic
- sql : flink sql query to define metrics
- sinks : where processed data or metrics are sent for further processing, storage, visualization, or analysis

## Stream
    
Let's say we have a kafka topic user_events with events looks like

    ```
    {"user_id": "952483", "item_id":"310884", "category_id": "4580532", "behavior": "pv", "ts": 1693122742}
    {"user_id": "794777", "item_id":"5119439", "category_id": "982926", "behavior": "pv", "ts": 1693122842}
    ...
    ```

### Register stream as a table

We need following configuration to register topic:
- SOURCE_KAFKA_TOPIC_NAMES : kafka topic name, in this case it's `user_events`
- INPUT_SCHEMA_TABLE : table name to be used for sql, in this case it's `user_behavior`
- INPUT_DATATYPE : datatype of records in kafka topic, in this it's `JSON`
- INPUT_SCHEMA_JSON_SCHEMA: json schema of records 
    ```json
          {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "type": "object",
          "properties": {
            "user_id": {
              "type": "string"
            },
            "item_id": {
              "type": "string"
            },
            "category_id": {
              "type": "string"
            },
            "behavior": {
              "type": "string"
            },
            "ts": {
              "type": "integer"
            }
          },
          "required": [
            "user_id",
            "item_id",
            "category_id",
            "behavior",
            "ts"
          ]
      }
  ```
- INPUT_SCHEMA_JSON_EVENT_TIMESTAMP_FIELD_NAME : field in record capture event time as epoch in second, in this case it's  `ts`
- INPUT_SCHEMA_EVENT_TIMESTAMP_FIELD_INDEX : index of event time column in record, in this case it's 4
- SOURCE_KAFKA_CONSUMER_CONFIG_BOOTSTRAP_SERVERS : kafka bootstrap server
- SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_COMMIT_ENABLE : enable auto offset commit
- SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET : configuration parameter to decide where to start consuming messages from
- SOURCE_KAFKA_CONSUMER_CONFIG_GROUP_ID : kafka consumer group id

### Sql query to define metric

The hourly trading volume is the number of “buy” behaviors completed each hour. 
Therefore, we can use a TUMBLE window function to assign data into hourly windows. 
Then, we count the number of “buy” records in each window. To implement this, we can filter out the “buy” data first 
and then apply COUNT(*).

```roomsql
    SELECT 
        HOUR(TUMBLE_START(rowtime, INTERVAL '1' HOUR)),
        COUNT(*)
        from user_behavior
        WHERE behavior = 'buy'  
        GROUP BY TUMBLE (rowtime, '1' HOUR)
```

### Register sink 

here we are registering influx to capture hourly buy trend and need following configuration:
- SINK_TYPE=influx
- SINK_INFLUX_URL=http://localhost:8086
- SINK_INFLUX_ORG=home
- SINK_INFLUX_BUCKET=temp
SINK_INFLUX_TOKEN= **********
SINK_INFLUX_BATCH_SIZE=100
SINK_INFLUX_FLUSH_DURATION_MS=5000
SINK_INFLUX_MEASUREMENT_NAME=orders_metrics
SINK_INFLUX_DB_NAME=metrics


## Job configuration

```text
# == Query ==
FLINK_SQL_QUERY=SELECT HOUR(TUMBLE_START(rowtime, INTERVAL '1' HOUR)), COUNT(*) from user_behavior WHERE behavior = 'buy' GROUP BY TUMBLE (rowtime, '1' HOUR)
FLINK_WATERMARK_INTERVAL_MS=60000
FLINK_WATERMARK_DELAY_MS=10000
# == Input Stream ==
STREAMS=[{"SOURCE_KAFKA_TOPIC_NAMES":"user_events","INPUT_SCHEMA_TABLE":"user_behavior","INPUT_DATATYPE":"JSON","INPUT_SCHEMA_JSON_SCHEMA":"{\\"$schema\\":\\"http://json-schema.org/draft-04/schema#\\",\\"type\\":\\"object\\",\\"properties\\":{\\"user_id\\":{\\"type\\":\\"string\\"},\\"item_id\\":{\\"type\\":\\"string\\"},\\"category_id\\":{\\"type\\":\\"string\\"},\\"behavior\\":{\\"type\\":\\"string\\"},\\"ts\\":{\\"type\\":\\"integer\\"}},\\"required\\":[\\"user_id\\",\\"item_id\\",\\"category_id\\",\\"behavior\\",\\"ts\\"]}", "INPUT_SCHEMA_JSON_EVENT_TIMESTAMP_FIELD_NAME": "ts", "INPUT_SCHEMA_EVENT_TIMESTAMP_FIELD_INDEX":"4", "SOURCE_KAFKA_CONSUMER_CONFIG_BOOTSTRAP_SERVERS":"localhost:9092","SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_COMMIT_ENABLE":"false","SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET":"earliest","SOURCE_KAFKA_CONSUMER_CONFIG_GROUP_ID":"group1","SOURCE_KAFKA_NAME":"local-kafka-stream","SOURCE_DETAILS":[{"SOURCE_TYPE":"UNBOUNDED","SOURCE_NAME":"KAFKA_CONSUMER"}]}]
# == Sink ==
SINK_TYPE=influx
SINK_INFLUX_URL=http://localhost:8086
SINK_INFLUX_ORG=home
SINK_INFLUX_BUCKET=temp
SINK_INFLUX_TOKEN=*****
SINK_INFLUX_BATCH_SIZE=100
SINK_INFLUX_FLUSH_DURATION_MS=5000
SINK_INFLUX_MEASUREMENT_NAME=orders_metrics
SINK_INFLUX_DB_NAME=metrics
```