package bike.rapido.metrics.sink.influx.errors;

import bike.rapido.metrics.exception.InfluxWriteException;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Valid exception.
 */
public class ValidException implements InfluxError {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidException.class.getName());
    private IOException exception;

    @Override
    public boolean hasException() {
        return true;
    }

    @Override
    public IOException getCurrentException() {
        return exception;
    }

    @Override
    public boolean filterError(Throwable throwable) {
        return throwable instanceof Exception;
    }

    @Override
    public void handle(Iterable<Point> points, Throwable throwable) {
        exception = new InfluxWriteException(throwable);
        logFailedPoints(points, LOGGER);
    }
}
