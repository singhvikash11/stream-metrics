package bike.rapido.metrics.utils;

public class Constants {

    public static final String FLINK_SQL_QUERY_KEY = "FLINK_SQL_QUERY";
    public static final String FLINK_SQL_QUERY_DEFAULT = "";

    public static final int FLINK_PARALLELISM_DEFAULT = 1;
    public static final String FLINK_PARALLELISM_KEY = "FLINK_PARALLELISM";
    public static final int FLINK_PARALLELISM_MAX_DEFAULT = 50;
    public static final String FLINK_PARALLELISM_MAX_KEY = "FLINK_PARALLELISM_MAX";
    public static final int FLINK_WATERMARK_INTERVAL_MS_DEFAULT = 10000;
    public static final String FLINK_WATERMARK_INTERVAL_MS_KEY = "FLINK_WATERMARK_INTERVAL_MS";
    public static final long FLINK_CHECKPOINT_INTERVAL_MS_DEFAULT = 30000;
    public static final String FLINK_CHECKPOINT_INTERVAL_MS_KEY = "FLINK_CHECKPOINT_INTERVAL_MS";
    public static final long FLINK_CHECKPOINT_TIMEOUT_MS_DEFAULT = 900000;
    public static final String FLINK_CHECKPOINT_TIMEOUT_MS_KEY = "FLINK_CHECKPOINT_TIMEOUT_MS";
    public static final long FLINK_CHECKPOINT_MIN_PAUSE_MS_DEFAULT = 5000;
    public static final String FLINK_CHECKPOINT_MIN_PAUSE_MS_KEY = "FLINK_CHECKPOINT_MIN_PAUSE_MS";
    public static final int FLINK_CHECKPOINT_MAX_CONCURRENT_DEFAULT = 1;
    public static final String FLINK_CHECKPOINT_MAX_CONCURRENT_KEY = "FLINK_CHECKPOINT_MAX_CONCURRENT";
    public static final int FLINK_RETENTION_IDLE_STATE_MINUTE_DEFAULT = 10;
    public static final String FLINK_RETENTION_IDLE_STATE_MINUTE_KEY = "FLINK_RETENTION_IDLE_STATE_MINUTE";
    public static final long FLINK_WATERMARK_DELAY_MS_DEFAULT = 10000;
    public static final String FLINK_WATERMARK_DELAY_MS_KEY = "FLINK_WATERMARK_DELAY_MS";
    public static final boolean FLINK_WATERMARK_PER_PARTITION_ENABLE_DEFAULT = false;
    public static final String FLINK_WATERMARK_PER_PARTITION_ENABLE_KEY = "FLINK_WATERMARK_PER_PARTITION_ENABLE";
    public static final String FLINK_JOB_ID_DEFAULT = "SQL Flink job";
    public static final String FLINK_JOB_ID_KEY = "FLINK_JOB_ID";

    public static final String SINK_KAFKA_TOPIC_KEY = "SINK_KAFKA_TOPIC";
    public static final String SINK_KAFKA_BROKERS_KEY = "SINK_KAFKA_BROKERS";
    public static final String SINK_KAFKA_PROTO_KEY = "SINK_KAFKA_PROTO_KEY";
    public static final String SINK_KAFKA_PROTO_MESSAGE_KEY = "SINK_KAFKA_PROTO_MESSAGE";
    public static final String SINK_KAFKA_STREAM_KEY = "SINK_KAFKA_STREAM";
    public static final String SINK_KAFKA_JSON_SCHEMA_KEY = "SINK_KAFKA_JSON_SCHEMA";
    public static final String SINK_KAFKA_DATA_TYPE = "SINK_KAFKA_DATA_TYPE";
    public static final String SINK_KAFKA_PRODUCE_LARGE_MESSAGE_ENABLE_KEY = "SINK_KAFKA_PRODUCE_LARGE_MESSAGE_ENABLE";
    public static final boolean SINK_KAFKA_PRODUCE_LARGE_MESSAGE_ENABLE_DEFAULT = false;
    public static final String SINK_KAFKA_COMPRESSION_TYPE_KEY = "compression.type";
    public static final String SINK_KAFKA_COMPRESSION_TYPE_DEFAULT = "snappy";
    public static final String SINK_KAFKA_MAX_REQUEST_SIZE_KEY = "max.request.size";
    public static final String SINK_KAFKA_MAX_REQUEST_SIZE_DEFAULT = "20971520";

    public static final String STREAM_INPUT_SCHEMA_EVENT_TIMESTAMP_FIELD_INDEX_KEY = "INPUT_SCHEMA_EVENT_TIMESTAMP_FIELD_INDEX";
    public static final String STREAM_SOURCE_KAFKA_TOPIC_NAMES_KEY = "SOURCE_KAFKA_TOPIC_NAMES";
    public static final String STREAM_INPUT_STREAM_NAME_KEY = "SOURCE_KAFKA_NAME";

    public static final String STREAM_SOURCE_DETAILS_KEY = "SOURCE_DETAILS";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_TYPE_KEY = "SOURCE_TYPE";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_TYPE_BOUNDED = "BOUNDED";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_TYPE_UNBOUNDED = "UNBOUNDED";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_NAME_KEY = "SOURCE_NAME";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_NAME_KAFKA = "KAFKA_SOURCE";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_NAME_PARQUET = "PARQUET_SOURCE";
    public static final String STREAM_SOURCE_DETAILS_SOURCE_NAME_KAFKA_CONSUMER = "KAFKA_CONSUMER";
    public static final String STREAM_SOURCE_PARQUET_FILE_PATHS_KEY = "SOURCE_PARQUET_FILE_PATHS";

    public static final String STREAM_SOURCE_PARQUET_FILE_DATE_RANGE_KEY = "SOURCE_PARQUET_FILE_DATE_RANGE";

    public static final String STREAM_INPUT_DATATYPE = "INPUT_DATATYPE";
    public static final String STREAM_INPUT_SCHEMA_JSON_EVENT_TIMESTAMP_FIELD_NAME_KEY = "INPUT_SCHEMA_JSON_EVENT_TIMESTAMP_FIELD_NAME";
    public static final String STREAM_INPUT_SCHEMA_JSON_SCHEMA_KEY = "INPUT_SCHEMA_JSON_SCHEMA";

    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET_KEY = "SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET";
    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET_DEFAULT = "latest";

    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_COMMIT_ENABLE_KEY = "SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_COMMIT_ENABLE";
    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_GROUP_ID_KEY = "SOURCE_KAFKA_CONSUMER_CONFIG_GROUP_ID";
    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_BOOTSTRAP_SERVERS_KEY = "SOURCE_KAFKA_CONSUMER_CONFIG_BOOTSTRAP_SERVERS";
    public static final String SOURCE_KAFKA_CONSUMER_CONFIG_SASL_JAAS_CONFIG_KEY = "SOURCE_KAFKA_CONSUMER_CONFIG_SASL_JAAS_CONFIG";

    public static final String SINK_INFLUX_LATE_RECORDS_DROPPED_KEY = "influx.late.records.dropped";
    public static final String SINK_INFLUX_DB_NAME_KEY = "SINK_INFLUX_DB_NAME";
    public static final String SINK_INFLUX_DB_NAME_DEFAULT = "";
    public static final String SINK_INFLUX_RETENTION_POLICY_KEY = "SINK_INFLUX_RETENTION_POLICY";
    public static final String SINK_INFLUX_RETENTION_POLICY_DEFAULT = "";
    public static final String SINK_INFLUX_MEASUREMENT_NAME_KEY = "SINK_INFLUX_MEASUREMENT_NAME";
    public static final String SINK_INFLUX_MEASUREMENT_NAME_DEFAULT = "";
    public static final String SINK_INFLUX_URL_KEY = "SINK_INFLUX_URL";
    public static final String SINK_INFLUX_URL_DEFAULT = "";
    public static final String SINK_INFLUX_BUCKET_KEY = "SINK_INFLUX_BUCKET";
    public static final String SINK_INFLUX_BUCKET_DEFAULT = "";
    public static final String SINK_INFLUX_ORG_KEY = "SINK_INFLUX_ORG";
    public static final String SINK_INFLUX_ORG_DEFAULT = "";
    public static final String SINK_INFLUX_TOKEN_KEY = "SINK_INFLUX_TOKEN";
    public static final String SINK_INFLUX_TOKEN_DEFAULT = "";
    public static final String SINK_INFLUX_BATCH_SIZE_KEY = "SINK_INFLUX_BATCH_SIZE";
    public static final int SINK_INFLUX_BATCH_SIZE_DEFAULT = 0;
    public static final String SINK_INFLUX_FLUSH_DURATION_MS_KEY = "SINK_INFLUX_FLUSH_DURATION_MS";
    public static final int SINK_INFLUX_FLUSH_DURATION_MS_DEFAULT = 0;

    public static final String SOURCE_KAFKA_CONSUME_LARGE_MESSAGE_ENABLE_KEY = "SOURCE_KAFKA_CONSUME_LARGE_MESSAGE_ENABLE";
    public static final boolean SOURCE_KAFKA_CONSUME_LARGE_MESSAGE_ENABLE_DEFAULT = false;
    public static final String SOURCE_KAFKA_MAX_PARTITION_FETCH_BYTES_KEY = "max.partition.fetch.bytes";
    public static final String SOURCE_KAFKA_MAX_PARTITION_FETCH_BYTES_DEFAULT = "5242880";

}
