package bike.rapido.metrics.exception.serde;

/**
 * The class Exception if failed on Deserialize the protobuf message.
 */
public class MetricDeserializationException extends RuntimeException {
    /**
     * Instantiates a new Metric deserialization exception.
     *
     * @param innerException the inner exception
     */
    public MetricDeserializationException(Exception innerException) {
        super(innerException);
    }

    public MetricDeserializationException(String message) {
        super(message);
    }
}
