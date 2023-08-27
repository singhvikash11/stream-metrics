package bike.rapido.metrics.source;


import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.exception.InvalidMetricSourceException;
import bike.rapido.metrics.serde.MetricDeserializer;
import bike.rapido.metrics.source.config.StreamConfig;
import bike.rapido.metrics.source.flinkkafkaconsumer.FlinkKafkaConsumerMetricSource;
import bike.rapido.metrics.source.kafka.KafkaMetricSource;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetricSourceFactory {

    public static MetricSource<Row> create(StreamConfig streamConfig, Configuration configuration, MetricDeserializer<Row> deserializer) {
        List<MetricSource<Row>> metricSources = getMetricSources(streamConfig, configuration, deserializer);
        return metricSources.stream()
                .filter(MetricSource::canBuild)
                .findFirst()
                .orElseThrow(() -> {
                    String sourceDetails = Arrays.toString(streamConfig.getSourceDetails());
                    InvalidMetricSourceException ex = new InvalidMetricSourceException(String.format("No suitable MetricSource can be created as per SOURCE_DETAILS config %s", sourceDetails));
                    return ex;
                });
    }

    private static List<MetricSource<Row>> getMetricSources(StreamConfig streamConfig, Configuration configuration, MetricDeserializer<Row> deserializer) {
        KafkaMetricSource kafkaMetricSource = new KafkaMetricSource(streamConfig, configuration, deserializer);
        FlinkKafkaConsumerMetricSource flinkKafkaConsumerMetricSource = new FlinkKafkaConsumerMetricSource(streamConfig, configuration, deserializer);
        return Stream.of(kafkaMetricSource, flinkKafkaConsumerMetricSource)
                .collect(Collectors.toList());
    }
}
