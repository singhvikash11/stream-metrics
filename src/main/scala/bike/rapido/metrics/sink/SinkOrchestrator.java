package bike.rapido.metrics.sink;

import bike.rapido.metrics.sink.influx.ErrorHandler;
import bike.rapido.metrics.sink.influx.InfluxDBFactoryWrapper;
import bike.rapido.metrics.sink.influx.InfluxDBSink;
import bike.rapido.metrics.sink.kafka.KafkaSerializationSchemaFactory;
import bike.rapido.metrics.sink.kafka.KafkaSerializerBuilder;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducerBase;
import bike.rapido.metrics.config.Configuration;
import org.apache.flink.types.Row;
import bike.rapido.metrics.sink.log.LogSink;

import java.util.*;

import static bike.rapido.metrics.utils.Constants.*;


/**
 * The Sink orchestrator.
 * Responsible for handling the sink type.
 */
public class SinkOrchestrator {
    private final Map<String, List<String>> metrics;

    public SinkOrchestrator() {
        this.metrics = new HashMap<>();
    }

    /**
     * Gets sink.
     *
     * @return the sink
     * @configuration configuration             the configuration
     * @columnNames columnNames               the column names
     * @StencilClientOrchestrator stencilClientOrchestrator the stencil client orchestrator
     */
    public Sink getSink(Configuration configuration, String[] columnNames) {
        String sinkType = configuration.getString("SINK_TYPE", "influx");
        Sink sink;
        switch (sinkType) {
            case "kafka":
                String outputBootStrapServers = configuration.getString(SINK_KAFKA_BROKERS_KEY, "");

                KafkaSerializerBuilder serializationSchema = KafkaSerializationSchemaFactory
                        .getSerializationSchema(configuration, columnNames);

                sink = (Sink) KafkaSink.<Row>builder()
                        .setBootstrapServers(outputBootStrapServers)
                        .setKafkaProducerConfig(getProducerProperties(configuration))
                        .setRecordSerializer(serializationSchema.build())
                        .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                        .build();

                break;
            case "influx":
                sink = new InfluxDBSink(new InfluxDBFactoryWrapper(), configuration, columnNames, new ErrorHandler());
                break;
            default:
                sink = new LogSink(columnNames);
        }
        return sink;
    }

    /**
     * Gets producer properties.
     *
     * @param configuration the configuration
     * @return the producer properties
     */
    protected Properties getProducerProperties(Configuration configuration) {
        String outputBrokerList = configuration.getString(SINK_KAFKA_BROKERS_KEY, "");
        Properties kafkaProducerConfigs = FlinkKafkaProducerBase.getPropertiesFromBrokerList(outputBrokerList);
        if (configuration.getBoolean(SINK_KAFKA_PRODUCE_LARGE_MESSAGE_ENABLE_KEY, SINK_KAFKA_PRODUCE_LARGE_MESSAGE_ENABLE_DEFAULT)) {
            kafkaProducerConfigs.setProperty(SINK_KAFKA_COMPRESSION_TYPE_KEY, SINK_KAFKA_COMPRESSION_TYPE_DEFAULT);
            kafkaProducerConfigs.setProperty(SINK_KAFKA_MAX_REQUEST_SIZE_KEY, SINK_KAFKA_MAX_REQUEST_SIZE_DEFAULT);
        }
        return kafkaProducerConfigs;
    }

    private void addMetric(String key, String value) {
        metrics.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
}
