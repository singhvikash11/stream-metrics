package bike.rapido.metrics.core;

/**
 * The interface for all the transformer.
 */
public interface Transformer {
    StreamInfo transform(StreamInfo streamInfo);
}
