package bike.rapido.metrics.exception.serde;

public class InvalidJSONSchemaException extends RuntimeException {
    public InvalidJSONSchemaException(Exception innerException) {
        super(innerException);
    }
}
