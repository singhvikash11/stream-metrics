package bike.rapido.metrics.config;

/**
 * The interface for all Configuration provider class.
 */
public interface ConfigurationProvider {
    /**
     * Get configuration.
     *
     * @return the configuration
     */
    Configuration get();
}
