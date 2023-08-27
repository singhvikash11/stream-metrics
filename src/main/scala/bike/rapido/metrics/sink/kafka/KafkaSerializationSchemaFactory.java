package bike.rapido.metrics.sink.kafka;


import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.sink.kafka.builder.KafkaJsonSerializerBuilder;

public class KafkaSerializationSchemaFactory {
    public static KafkaSerializerBuilder getSerializationSchema(Configuration configuration, String[] columnNames) {
        return new KafkaJsonSerializerBuilder(configuration);
    }
}
