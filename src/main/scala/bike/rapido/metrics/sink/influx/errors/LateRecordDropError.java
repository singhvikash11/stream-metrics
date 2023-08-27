package bike.rapido.metrics.sink.influx.errors;

import bike.rapido.metrics.utils.Constants;
import com.influxdb.client.write.Point;
import org.apache.flink.api.connector.sink.Sink.InitContext;
import org.apache.flink.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Late record drop error.
 */
public class LateRecordDropError implements InfluxError {
    private final Counter counter;
    private static final Logger LOGGER = LoggerFactory.getLogger(LateRecordDropError.class.getName());
    private static final String PREFIX = "{\"error\":\"partial write: points beyond retention policy dropped=";

    /**
     * Instantiates a new Late record drop error.
     *
     * @param initContext the context available in sink functions
     */
    public LateRecordDropError(InitContext initContext) {
        this.counter = initContext.metricGroup()
                .addGroup(Constants.SINK_INFLUX_LATE_RECORDS_DROPPED_KEY).counter("value");
    }

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
        return isLateDropping(throwable);
    }

    @Override
    public void handle(Iterable<Point> points, Throwable throwable) {
        reportDroppedPoints(parseDroppedPointsCount(throwable));
        logFailedPoints(points, LOGGER);
    }

    private void reportDroppedPoints(int numPoints) {
        counter.inc(numPoints);
        LOGGER.warn("Numbers of Points Dropped :" + numPoints);
    }

    private int parseDroppedPointsCount(Throwable throwable) {
        String[] split = throwable.getMessage().split("=");
        return Integer.parseInt(split[1].trim().replace("\"}", ""));
    }

    private boolean isLateDropping(Throwable throwable) {
        return throwable instanceof Exception
                && throwable.getMessage().startsWith(PREFIX);
    }
}
