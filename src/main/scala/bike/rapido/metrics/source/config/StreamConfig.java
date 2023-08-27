package bike.rapido.metrics.source.config;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.source.config.models.SourceDetails;
import bike.rapido.metrics.source.config.models.SourceName;
import bike.rapido.metrics.source.config.models.SourceType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import lombok.Getter;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static bike.rapido.metrics.core.Constants.*;
import static bike.rapido.metrics.utils.Constants.*;


public class StreamConfig {
    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private static final String KAFKA_PREFIX = "source_kafka_consumer_config_";

    @SerializedName(STREAM_SOURCE_KAFKA_TOPIC_NAMES_KEY)
    @Getter
    private String kafkaTopicNames;

    @SerializedName(STREAM_INPUT_SCHEMA_TABLE)
    @Getter
    private String schemaTable;

    @SerializedName(SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_COMMIT_ENABLE_KEY)
    @Getter
    private String autoCommitEnable;

    @SerializedName(SOURCE_KAFKA_CONSUMER_CONFIG_AUTO_OFFSET_RESET_KEY)
    private String autoOffsetReset;

    @SerializedName(SOURCE_KAFKA_CONSUMER_CONFIG_GROUP_ID_KEY)
    @Getter
    private String consumerGroupId;

    @SerializedName(SOURCE_KAFKA_CONSUMER_CONFIG_BOOTSTRAP_SERVERS_KEY)
    @Getter
    private String bootstrapServers;

    @SerializedName(STREAM_INPUT_STREAM_NAME_KEY)
    @Getter
    private String kafkaName;

    @SerializedName(STREAM_INPUT_SCHEMA_JSON_SCHEMA_KEY)
    @Getter
    private String jsonSchema;

    @SerializedName(STREAM_INPUT_SCHEMA_JSON_EVENT_TIMESTAMP_FIELD_NAME_KEY)
    @Getter
    private String jsonEventTimestampFieldName;

    @SerializedName(STREAM_INPUT_SCHEMA_EVENT_TIMESTAMP_FIELD_INDEX_KEY)
    @Getter
    private String eventTimestampFieldIndex;

    @SerializedName(STREAM_INPUT_DATATYPE)
    private String dataType;

    @SerializedName(STREAM_SOURCE_DETAILS_KEY)
    private SourceDetails[] sourceDetails;

    public String getDataType() {
        if (dataType == null) {
            dataType = "PROTO";
        }
        return dataType;
    }

    public SourceDetails[] getSourceDetails() {
        if (sourceDetails == null) {
            return new SourceDetails[]{new SourceDetails(SourceName.KAFKA_CONSUMER, SourceType.UNBOUNDED)};
        } else {
            return sourceDetails;
        }
    }

    public String getAutoOffsetReset() {
        if (autoOffsetReset == null) {
            autoOffsetReset = "latest";
        }
        return autoOffsetReset;
    }

    public static StreamConfig[] parse(Configuration configuration) {
        String jsonArrayString = configuration.getString(INPUT_STREAMS, "");
        JsonReader reader = new JsonReader(new StringReader(jsonArrayString));
        reader.setLenient(true);

        return Stream.of(GSON.fromJson(jsonArrayString, StreamConfig[].class))
                .map(StreamConfigValidator::validateSourceDetails)
                .toArray(StreamConfig[]::new);
    }

    public Properties getKafkaProps(Configuration configuration) {
        String jsonString = GSON.toJson(this);
        Map<String, String> streamConfigMap = GSON.fromJson(jsonString, Map.class);
        Properties kafkaProps = new Properties();
        streamConfigMap.entrySet()
                .stream()
                .filter(e -> e.getKey().toLowerCase().startsWith(KAFKA_PREFIX))
                .forEach(e -> kafkaProps.setProperty(parseVarName(e.getKey(), KAFKA_PREFIX), e.getValue()));
        setAdditionalConfigs(kafkaProps, configuration);
        return kafkaProps;
    }

    private String parseVarName(String varName, String kafkaPrefix) {
        String[] names = varName.toLowerCase().replaceAll(kafkaPrefix, "").split("_");
        return String.join(".", names);
    }

    private void setAdditionalConfigs(Properties kafkaProps, Configuration configuration) {
        if (configuration.getBoolean(SOURCE_KAFKA_CONSUME_LARGE_MESSAGE_ENABLE_KEY, SOURCE_KAFKA_CONSUME_LARGE_MESSAGE_ENABLE_DEFAULT)) {
            kafkaProps.setProperty(SOURCE_KAFKA_MAX_PARTITION_FETCH_BYTES_KEY, SOURCE_KAFKA_MAX_PARTITION_FETCH_BYTES_DEFAULT);
        }
    }

    public Pattern getTopicPattern() {
        return Pattern.compile(kafkaTopicNames);
    }

    public OffsetsInitializer getStartingOffset() {
        return OffsetsInitializer.committedOffsets(getOffsetResetStrategy());
    }

    private OffsetResetStrategy getOffsetResetStrategy() {
        return OffsetResetStrategy.valueOf(autoOffsetReset.toUpperCase());
    }
}
