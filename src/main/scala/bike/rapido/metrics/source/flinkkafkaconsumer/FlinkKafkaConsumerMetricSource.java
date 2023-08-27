package bike.rapido.metrics.source.flinkkafkaconsumer;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.serde.MetricDeserializer;
import bike.rapido.metrics.source.MetricSource;
import bike.rapido.metrics.source.config.StreamConfig;
import bike.rapido.metrics.source.config.models.SourceDetails;
import bike.rapido.metrics.source.config.models.SourceName;
import bike.rapido.metrics.source.config.models.SourceType;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.types.Row;

import static bike.rapido.metrics.source.config.models.SourceName.KAFKA_CONSUMER;
import static bike.rapido.metrics.source.config.models.SourceType.UNBOUNDED;

public class FlinkKafkaConsumerMetricSource implements MetricSource<Row> {

    private final MetricDeserializer<Row> deserializer;
    private final StreamConfig streamConfig;
    private final Configuration configuration;
    private static final SourceName SUPPORTED_SOURCE_NAME = KAFKA_CONSUMER;
    private static final SourceType SUPPORTED_SOURCE_TYPE = UNBOUNDED;

    public FlinkKafkaConsumerMetricSource(StreamConfig streamConfig, Configuration configuration, MetricDeserializer<Row> deserializer) {
        this.streamConfig = streamConfig;
        this.configuration = configuration;
        this.deserializer = deserializer;
    }

    FlinkKafkaConsumerCustom buildSource() {
        KafkaDeserializationSchema kafkaDeserializationSchema = (KafkaDeserializationSchema<Row>) deserializer;
        return new FlinkKafkaConsumerCustom(streamConfig.getTopicPattern(),
                kafkaDeserializationSchema, streamConfig.getKafkaProps(configuration), configuration);
    }

    @Override
    public DataStream<Row> register(StreamExecutionEnvironment executionEnvironment, WatermarkStrategy<Row> watermarkStrategy) {
        FlinkKafkaConsumerCustom source = buildSource();
        return executionEnvironment.addSource(source.assignTimestampsAndWatermarks(watermarkStrategy));
    }

    @Override
    public boolean canBuild() {
        SourceDetails[] sourceDetailsArray = streamConfig.getSourceDetails();
        if (sourceDetailsArray.length != 1) {
            return false;
        } else {
            SourceName sourceName = sourceDetailsArray[0].getSourceName();
            SourceType sourceType = sourceDetailsArray[0].getSourceType();
            return sourceName.equals(SUPPORTED_SOURCE_NAME) && sourceType.equals(SUPPORTED_SOURCE_TYPE)
                    && deserializer instanceof KafkaDeserializationSchema;
        }
    }
}
