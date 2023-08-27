package bike.rapido.metrics.exception;

/**
 * The class Exception if there is something wrong with Metric configuration.
 */
public class MetricConfigurationException extends RuntimeException {

    /**
     * Instantiates a new Metric configuration exception with specified error message.
     *
     * @param message the message
     */
    public MetricConfigurationException(String message) {
        super(message);
    }
}
