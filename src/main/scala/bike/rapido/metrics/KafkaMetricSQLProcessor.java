package bike.rapido.metrics;

import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.config.ConfigurationProvider;
import bike.rapido.metrics.config.ConfigurationProviderFactory;
import bike.rapido.metrics.core.StreamContext;
import org.apache.flink.client.program.ProgramInvocationException;

import java.util.TimeZone;

/**
 * Main class to run Metric.
 */
public class KafkaMetricSQLProcessor {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws ProgramInvocationException the program invocation exception
     */
    public static void main(String[] args) throws ProgramInvocationException {
        try {
            ConfigurationProvider provider = new ConfigurationProviderFactory(args).provider();
            Configuration configuration = provider.get();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            StreamContext streamContext = StreamContext.init(configuration);
            StreamManager streamManager = new StreamManager(streamContext);
            streamManager
                    .registerConfigs()
                    .registerSourceWithPreProcessors()
                    .registerOutputStream()
                    .execute();
        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            throw new ProgramInvocationException(e);
        }
    }
}
