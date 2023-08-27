package bike.rapido.metrics.source.config.models;

import com.google.gson.annotations.SerializedName;

import static bike.rapido.metrics.utils.Constants.*;

public enum SourceName {
    @SerializedName(STREAM_SOURCE_DETAILS_SOURCE_NAME_KAFKA)
    KAFKA_SOURCE,
    @SerializedName(STREAM_SOURCE_DETAILS_SOURCE_NAME_KAFKA_CONSUMER)
    KAFKA_CONSUMER
}
