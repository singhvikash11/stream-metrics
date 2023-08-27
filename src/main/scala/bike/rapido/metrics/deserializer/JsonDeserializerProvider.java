package bike.rapido.metrics.deserializer;

import bike.rapido.metrics.serde.DataTypes;
import bike.rapido.metrics.serde.MetricDeserializer;
import bike.rapido.metrics.serde.json.deserialization.JsonDeserializer;
import bike.rapido.metrics.source.config.models.SourceDetails;
import bike.rapido.metrics.source.config.models.SourceName;
import org.apache.flink.types.Row;
import bike.rapido.metrics.source.config.StreamConfig;

import java.util.Arrays;
import java.util.HashSet;

import static bike.rapido.metrics.serde.DataTypes.JSON;
import static bike.rapido.metrics.source.config.models.SourceName.KAFKA_CONSUMER;
import static bike.rapido.metrics.source.config.models.SourceName.KAFKA_SOURCE;


public class JsonDeserializerProvider implements MetricDeserializerProvider<Row> {
    private final StreamConfig streamConfig;
    private static final HashSet<SourceName> COMPATIBLE_SOURCES = new HashSet<>(Arrays.asList(KAFKA_SOURCE, KAFKA_CONSUMER));
    private static final DataTypes COMPATIBLE_INPUT_SCHEMA_TYPE = JSON;

    public JsonDeserializerProvider(StreamConfig streamConfig) {
        this.streamConfig = streamConfig;
    }

    @Override
    public MetricDeserializer<Row> getMetricDeserializer() {
        return new JsonDeserializer(streamConfig.getJsonSchema(), streamConfig.getJsonEventTimestampFieldName());
    }

    @Override
    public boolean canProvide() {
        SourceDetails[] sourceDetailsList = streamConfig.getSourceDetails();
        for (SourceDetails sourceDetails : sourceDetailsList) {
            SourceName sourceName = sourceDetails.getSourceName();
            DataTypes inputSchemaType = DataTypes.valueOf(streamConfig.getDataType());
            if (!COMPATIBLE_SOURCES.contains(sourceName) || !inputSchemaType.equals(COMPATIBLE_INPUT_SCHEMA_TYPE)) {
                return false;
            }
        }
        return true;
    }
}
