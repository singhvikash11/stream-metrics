package bike.rapido.metrics.deserializer;

import bike.rapido.metrics.serde.MetricDeserializer;

public interface MetricDeserializerProvider<D> {
    MetricDeserializer<D> getMetricDeserializer();

    boolean canProvide();
}
