package bike.rapido.metrics.exception;

import java.io.IOException;

public class InfluxWriteException extends IOException {
    public InfluxWriteException(Throwable err) {
        super(err);
    }
}
