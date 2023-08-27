package bike.rapido.metrics.source;


import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.deserializer.MetricDeserializerFactory;
import bike.rapido.metrics.serde.MetricDeserializer;
import bike.rapido.metrics.source.config.StreamConfig;
import lombok.Getter;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.io.Serializable;

public class Stream implements Serializable {
    @Getter
    private final MetricSource<Row> metricSource;
    @Getter
    private final String streamName;

    Stream(MetricSource<Row> metricSource, String streamName) {
        this.metricSource = metricSource;
        this.streamName = streamName;
    }

    public DataStream<Row> registerSource(StreamExecutionEnvironment executionEnvironment, WatermarkStrategy<Row> watermarkStrategy) {
        return metricSource.register(executionEnvironment, watermarkStrategy);
    }

    public static class Builder {
        private final StreamConfig streamConfig;
        private final Configuration configuration;

        public Builder(StreamConfig streamConfig, Configuration configuration) {
            this.streamConfig = streamConfig;
            this.configuration = configuration;
        }

        public Stream build() {
            MetricDeserializer<Row> metricDeserializer = MetricDeserializerFactory.create(streamConfig, configuration);
            MetricSource<Row> metricSource = MetricSourceFactory.create(streamConfig, configuration, metricDeserializer);
            return new Stream(metricSource, streamConfig.getSchemaTable());
        }
    }
}
