package bike.rapido.metrics.sink.influx.errors;

import com.influxdb.client.write.Point;

import java.io.IOException;

/**
 * No error found on Influx sink.
 */
public class NoError implements InfluxError {
    @Override
    public boolean hasException() {
        return false;
    }

    @Override
    public IOException getCurrentException() {
        return null;
    }

    @Override
    public boolean filterError(Throwable throwable) {
        return false;
    }

    @Override
    public void handle(Iterable<Point> points, Throwable throwable) {
    }
}
