package bike.rapido.metrics.exception.serde;

/**
 * The class Exception if failed on Serializing the protobuf message.
 */
public class MetricSerializationException extends RuntimeException {
    /**
     * Instantiates a new Metric serialization exception.
     *
     * @param protoClassMisconfiguredError the proto class misconfigured error
     */
    public MetricSerializationException(String protoClassMisconfiguredError) {
        super(protoClassMisconfiguredError);
    }
}
