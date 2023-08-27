package bike.rapido.metrics.deserializer;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.exception.MetricConfigurationException;
import bike.rapido.metrics.serde.MetricDeserializer;

import bike.rapido.metrics.source.config.StreamConfig;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetricDeserializerFactory {
    public static MetricDeserializer<Row> create(StreamConfig streamConfig, Configuration configuration) {
        return getMetricDeserializerProviders(streamConfig, configuration)
                .stream()
                .filter(MetricDeserializerProvider::canProvide)
                .findFirst()
                .orElseThrow(() -> {
                    MetricConfigurationException ex = new MetricConfigurationException("No suitable deserializer could be constructed for the given stream configuration.");
                    return ex;
                })
                .getMetricDeserializer();
    }

    private static List<MetricDeserializerProvider<Row>> getMetricDeserializerProviders(StreamConfig streamConfig, Configuration configuration) {
        return Stream.of(
                        new JsonDeserializerProvider(streamConfig))
                .collect(Collectors.toList());
    }
}
