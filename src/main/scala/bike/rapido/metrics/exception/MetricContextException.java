package bike.rapido.metrics.exception;

/**
 * The class Exception if there is something wrong with Metric context object.
 */
public class MetricContextException extends RuntimeException {

    /**
     * Instantiates a new Metric context exception with specified error message.
     *
     * @param message the message
     */
    public MetricContextException(String message) {
        super(message);
    }
}
