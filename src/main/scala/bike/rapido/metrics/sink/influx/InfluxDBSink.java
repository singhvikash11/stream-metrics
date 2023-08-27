package bike.rapido.metrics.sink.influx;

import bike.rapido.metrics.config.Configuration;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import org.apache.flink.api.connector.sink.Committer;
import org.apache.flink.api.connector.sink.GlobalCommitter;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.api.connector.sink.SinkWriter;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static bike.rapido.metrics.utils.Constants.*;


public class InfluxDBSink implements Sink<Row, Void, Void, Void> {
    private InfluxDBFactoryWrapper influxDBFactory;
    private Configuration configuration;
    private String[] columnNames;
    private ErrorHandler errorHandler;

    public InfluxDBSink(InfluxDBFactoryWrapper influxDBFactory, Configuration configuration, String[] columnNames, ErrorHandler errorHandler) {
        this.influxDBFactory = influxDBFactory;
        this.configuration = configuration;
        this.columnNames = columnNames;
        this.errorHandler = errorHandler;
    }

    @Override
    public SinkWriter<Row, Void, Void> createWriter(InitContext context, List<Void> states) throws IOException {
        InfluxDBClient influxDB = influxDBFactory.connect(configuration.getString(SINK_INFLUX_URL_KEY, SINK_INFLUX_URL_DEFAULT),
                configuration.getString(SINK_INFLUX_ORG_KEY, SINK_INFLUX_ORG_DEFAULT),
                configuration.getString(SINK_INFLUX_BUCKET_KEY, SINK_INFLUX_BUCKET_DEFAULT),
                configuration.getString(SINK_INFLUX_TOKEN_KEY, SINK_INFLUX_TOKEN_DEFAULT));
        errorHandler.init(context);

        WriteOptions writeOptions = WriteOptions.builder().
                batchSize(configuration.getInteger(SINK_INFLUX_BATCH_SIZE_KEY, SINK_INFLUX_BATCH_SIZE_DEFAULT)).
                flushInterval(configuration.getInteger(SINK_INFLUX_FLUSH_DURATION_MS_KEY, SINK_INFLUX_FLUSH_DURATION_MS_DEFAULT)).
                maxRetries(3).
                build();

        WriteApi writeApi = influxDB.makeWriteApi(writeOptions);

        return new InfluxDBWriter(configuration, writeApi, columnNames, errorHandler);
    }

    @Override
    public Optional<SimpleVersionedSerializer<Void>> getWriterStateSerializer() {
        return Optional.empty();
    }

    @Override
    public Optional<Committer<Void>> createCommitter() throws IOException {
        return Optional.empty();
    }

    @Override
    public Optional<GlobalCommitter<Void, Void>> createGlobalCommitter() throws IOException {
        return Optional.empty();
    }

    @Override
    public Optional<SimpleVersionedSerializer<Void>> getCommittableSerializer() {
        return Optional.empty();
    }

    @Override
    public Optional<SimpleVersionedSerializer<Void>> getGlobalCommittableSerializer() {
        return Optional.empty();
    }

}
