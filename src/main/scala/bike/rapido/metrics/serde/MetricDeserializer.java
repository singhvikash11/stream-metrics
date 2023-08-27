package bike.rapido.metrics.serde;

import org.apache.flink.api.java.typeutils.ResultTypeQueryable;

import java.io.Serializable;

public interface MetricDeserializer<T> extends Serializable, ResultTypeQueryable<T> {

}
