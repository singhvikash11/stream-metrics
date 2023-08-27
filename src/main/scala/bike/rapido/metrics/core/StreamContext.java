package bike.rapido.metrics.core;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.exception.MetricContextException;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MetricContext singleton object.
 * It initializes with StreamExecutionEnvironment, StreamTableEnvironment and Configuration.
 */
public class StreamContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamContext.class.getName());
    private static volatile StreamContext metricContext = null;
    private final StreamExecutionEnvironment executionEnvironment;
    private final StreamTableEnvironment tableEnvironment;
    private final Configuration configuration;

    /**
     * Instantiates a new MetricContext.
     *
     * @param configuration the Configuration
     */
    private StreamContext(Configuration configuration) {
        this.executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().build();
        tableEnvironment = StreamTableEnvironment.create(executionEnvironment, environmentSettings);
        this.configuration = configuration;
    }

    /**
     * Get the instance of MetricContext.
     */
    public static StreamContext getInstance() {
        if (metricContext == null) {
            throw new MetricContextException("MetricContext object is not initialized");
        }
        return metricContext;
    }

    /**
     * Initialization of a new MetricContext.
     *
     * @param configuration the Configuration
     */
    public static synchronized StreamContext init(Configuration configuration) {
        if (metricContext != null) {
            throw new MetricContextException("MetricContext object is already initialized");
        }
        metricContext = new StreamContext(configuration);
        LOGGER.info("MetricContext is initialized");
        return metricContext;
    }

    public StreamExecutionEnvironment getExecutionEnvironment() {
        return executionEnvironment;
    }

    public StreamTableEnvironment getTableEnvironment() {
        return tableEnvironment;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
