package bike.rapido.metrics.sink.kafka.builder;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.exception.serde.InvalidJSONSchemaException;
import bike.rapido.metrics.sink.kafka.KafkaSerializerBuilder;
import bike.rapido.metrics.utils.Constants;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.formats.json.JsonRowSchemaConverter;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaJsonSerializerBuilder implements KafkaSerializerBuilder {
    private Map<String, List<String>> metrics;
    private Configuration configuration;

    public KafkaJsonSerializerBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.metrics = new HashMap<>();
    }

    @Override
    public KafkaRecordSerializationSchema build() {
        String outputTopic = configuration.getString(Constants.SINK_KAFKA_TOPIC_KEY, "");
        String outputJsonSchema = configuration.getString(Constants.SINK_KAFKA_JSON_SCHEMA_KEY, "");

        try {
            TypeInformation<Row> opTypeInfo = JsonRowSchemaConverter.convert(outputJsonSchema);
            JsonRowSerializationSchema jsonRowSerializationSchema = JsonRowSerializationSchema
                    .builder()
                    .withTypeInfo(opTypeInfo)
                    .build();
            return KafkaRecordSerializationSchema
                    .builder()
                    .setValueSerializationSchema(jsonRowSerializationSchema)
                    .setTopic(outputTopic)
                    .build();
        } catch (IllegalArgumentException exception) {
            throw new InvalidJSONSchemaException(exception);
        }
    }

}
