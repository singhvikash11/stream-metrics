package bike.rapido.metrics.sink.influx;

import bike.rapido.metrics.config.Configuration;
import com.google.common.base.Strings;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.flink.api.connector.sink.SinkWriter;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bike.rapido.metrics.utils.Constants.*;

public class InfluxDBWriter implements SinkWriter<Row, Void, Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBWriter.class.getName());
    private final String databaseName;
    private final String retentionPolicy;
    private final String measurementName;
    private WriteApi writeApi;

    private String[] columnNames;
    private ErrorHandler errorHandler;

    public InfluxDBWriter(Configuration configuration, WriteApi influxDB, String[] columnNames, ErrorHandler errorHandler) {
        databaseName = configuration.getString(SINK_INFLUX_DB_NAME_KEY, SINK_INFLUX_DB_NAME_DEFAULT);
        retentionPolicy = configuration.getString(SINK_INFLUX_RETENTION_POLICY_KEY, SINK_INFLUX_RETENTION_POLICY_DEFAULT);
        measurementName = configuration.getString(SINK_INFLUX_MEASUREMENT_NAME_KEY, SINK_INFLUX_MEASUREMENT_NAME_DEFAULT);
        this.writeApi = influxDB;
        this.columnNames = columnNames;
        this.errorHandler = errorHandler;
    }

    @Override
    public void write(Row row, Context context) throws IOException, InterruptedException {
        LOGGER.info("row to influx: " + row);

        Point point = Point.measurement(measurementName);
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            if (columnName.equals("window_timestamp")) {
                LocalDateTime timeField = (LocalDateTime) row.getField(i);
                ZonedDateTime zonedDateTime = timeField.atZone(ZoneOffset.UTC);
                point.time(zonedDateTime.toInstant().toEpochMilli(), WritePrecision.MS);
            } else if (columnName.startsWith("tag_")) {
                point.addTag(columnName, String.valueOf(row.getField(i)));
            } else if (columnName.startsWith("label_")) {
                point.addTag(columnName.substring("label_".length()), ((String) row.getField(i)));
            } else {
                if (!(Strings.isNullOrEmpty(columnName) || row.getField(i) == null)) {
                    fields.put(columnName, row.getField(i));
                }
            }
        }

        try {
            writeApi.writePoint(point.addFields(fields));
        } catch (Exception exception) {
            throw exception;
        }
    }

    @Override
    public List<Void> prepareCommit(boolean flush) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public void close() throws Exception {
        writeApi.close();
    }


    @Override
    public List<Void> snapshotState(long checkpointId) throws IOException {
        try {
            writeApi.flush();
        } catch (Exception exception) {
            throw exception;
        }
        return Collections.emptyList();
    }
}
