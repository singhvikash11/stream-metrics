package bike.rapido.metrics;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.core.StreamContext;
import bike.rapido.metrics.core.StreamInfo;
import bike.rapido.metrics.sink.SinkOrchestrator;
import bike.rapido.metrics.source.StreamsFactory;
import bike.rapido.metrics.utils.Constants;
import bike.rapido.metrics.watermark.LastColumnWatermark;
import bike.rapido.metrics.watermark.NoWatermark;
import bike.rapido.metrics.watermark.StreamWatermarkAssigner;
import bike.rapido.metrics.watermark.WatermarkStrategyDefinition;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ApiExpression;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.time.Duration;

import static bike.rapido.metrics.utils.Constants.*;
import static org.apache.flink.table.api.Expressions.$;


/**
 * The Stream manager.
 */
public class StreamManager {

    private final Configuration configuration;
    private final StreamExecutionEnvironment executionEnvironment;
    private final StreamTableEnvironment tableEnvironment;
    private final StreamContext streamContext;

    /**
     * Instantiates a new Stream manager.
     *
     * @param streamContext the metricContext in form of param
     */
    public StreamManager(StreamContext streamContext) {
        this.streamContext = streamContext;
        this.configuration = streamContext.getConfiguration();
        this.executionEnvironment = streamContext.getExecutionEnvironment();
        this.tableEnvironment = streamContext.getTableEnvironment();
    }

    /**
     * Register configs stream manager.
     *
     * @return the stream manager
     */
    public StreamManager registerConfigs() {
        executionEnvironment.setMaxParallelism(configuration.getInteger(Constants.FLINK_PARALLELISM_MAX_KEY, Constants.FLINK_PARALLELISM_MAX_DEFAULT));
        executionEnvironment.setParallelism(configuration.getInteger(FLINK_PARALLELISM_KEY, FLINK_PARALLELISM_DEFAULT));
        executionEnvironment.getConfig().setAutoWatermarkInterval(configuration.getInteger(FLINK_WATERMARK_INTERVAL_MS_KEY, FLINK_WATERMARK_INTERVAL_MS_DEFAULT));
        executionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(Integer.MAX_VALUE);
        executionEnvironment.enableCheckpointing(configuration.getLong(FLINK_CHECKPOINT_INTERVAL_MS_KEY, FLINK_CHECKPOINT_INTERVAL_MS_DEFAULT));
        executionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        executionEnvironment.getCheckpointConfig().setCheckpointTimeout(configuration.getLong(FLINK_CHECKPOINT_TIMEOUT_MS_KEY, FLINK_CHECKPOINT_TIMEOUT_MS_DEFAULT));
        executionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(configuration.getLong(FLINK_CHECKPOINT_MIN_PAUSE_MS_KEY, FLINK_CHECKPOINT_MIN_PAUSE_MS_DEFAULT));
        executionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(configuration.getInteger(FLINK_CHECKPOINT_MAX_CONCURRENT_KEY, FLINK_CHECKPOINT_MAX_CONCURRENT_DEFAULT));
        executionEnvironment.getConfig().setGlobalJobParameters(configuration.getParam());

        tableEnvironment.getConfig().setIdleStateRetention(Duration.ofMinutes(configuration.getInteger(FLINK_RETENTION_IDLE_STATE_MINUTE_KEY, FLINK_RETENTION_IDLE_STATE_MINUTE_DEFAULT)));
        return this;
    }

    /**
     * Register source with pre-processors stream manager.
     *
     * @return the stream manager
     */
    public StreamManager registerSourceWithPreProcessors() {
        long watermarkDelay = configuration.getLong(FLINK_WATERMARK_DELAY_MS_KEY, FLINK_WATERMARK_DELAY_MS_DEFAULT);
        Boolean enablePerPartitionWatermark = configuration.getBoolean(FLINK_WATERMARK_PER_PARTITION_ENABLE_KEY, FLINK_WATERMARK_PER_PARTITION_ENABLE_DEFAULT);
        StreamsFactory.getStreams(configuration)
                .forEach(stream -> {
                    String tableName = stream.getStreamName();
                    WatermarkStrategyDefinition watermarkStrategyDefinition = getSourceWatermarkDefinition(enablePerPartitionWatermark);
                    DataStream<Row> dataStream = stream.registerSource(executionEnvironment, watermarkStrategyDefinition.getWatermarkStrategy(watermarkDelay));
                    StreamWatermarkAssigner streamWatermarkAssigner = new StreamWatermarkAssigner(new LastColumnWatermark());

                    DataStream<Row> rowSingleOutputStreamOperator = streamWatermarkAssigner
                            .assignTimeStampAndWatermark(dataStream, watermarkDelay, enablePerPartitionWatermark);

                    Table table = tableEnvironment.fromDataStream(rowSingleOutputStreamOperator, getApiExpressions(dataStream.getType()));

                    tableEnvironment.createTemporaryView(tableName, table);
                });
        return this;
    }

    private WatermarkStrategyDefinition getSourceWatermarkDefinition(Boolean enablePerPartitionWatermark) {
        return enablePerPartitionWatermark ? new LastColumnWatermark() : new NoWatermark();
    }

    private ApiExpression[] getApiExpressions(TypeInformation<Row> streamInfo) {
        //String rowTimeAttributeName = configuration.getString(FLINK_ROWTIME_ATTRIBUTE_NAME_KEY, FLINK_ROWTIME_ATTRIBUTE_NAME_DEFAULT);
        String[] columnNames = ((RowTypeInfo) streamInfo).getFieldNames();
        ApiExpression[] expressions = new ApiExpression[columnNames.length];

        for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
            String columnName = columnNames[colIndex];
            if (colIndex == columnNames.length - 1) {
                expressions[columnNames.length - 1] = $(columnName).rowtime();
            } else {
                ApiExpression expression = $(columnNames[colIndex]);
                expressions[colIndex] = expression;
            }
        }
        return expressions;
    }


    /**
     * Register output stream manager.
     *
     * @return the stream manager
     */
    public StreamManager registerOutputStream() {
        Table table = tableEnvironment.sqlQuery(configuration.getString(Constants.FLINK_SQL_QUERY_KEY, Constants.FLINK_SQL_QUERY_DEFAULT));
        StreamInfo streamInfo = createStreamInfo(table);
        addSink(streamInfo);
        return this;
    }

    /**
     * Execute metric job.
     *
     * @throws Exception the exception
     */
    public void execute() throws Exception {
        executionEnvironment.execute(configuration.getString(FLINK_JOB_ID_KEY, FLINK_JOB_ID_DEFAULT));
    }

    /**
     * Create stream info.
     *
     * @param table the table
     * @return the stream info
     */
    protected StreamInfo createStreamInfo(Table table) {
        DataStream<Row> stream = tableEnvironment
                .toRetractStream(table, Row.class)
                .filter(value -> value.f0)
                .map(value -> value.f1);
        return new StreamInfo(stream, table.getSchema().getFieldNames());
    }

    private void addSink(StreamInfo streamInfo) {
        SinkOrchestrator sinkOrchestrator = new SinkOrchestrator();
        streamInfo.getDataStream().sinkTo(sinkOrchestrator.getSink(configuration, streamInfo.getColumnNames()));
    }

}
