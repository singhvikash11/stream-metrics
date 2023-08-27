package bike.rapido.metrics.sink.influx.errors;

import bike.rapido.metrics.exception.InfluxWriteException;
import bike.rapido.metrics.sink.influx.InfluxDBSink;
import com.influxdb.client.write.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Valid error.
 */
public class ValidError implements InfluxError {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBSink.class.getName());
    private IOException error;

    @Override
    public boolean hasException() {
        return true;
    }

    @Override
    public IOException getCurrentException() {
        return error;
    }

    @Override
    public boolean filterError(Throwable throwable) {
        return throwable instanceof Error;
    }

    @Override
    public void handle(Iterable<Point> points, Throwable throwable) {
        error = new InfluxWriteException(throwable);
        logFailedPoints(points, LOGGER);
    }
}
