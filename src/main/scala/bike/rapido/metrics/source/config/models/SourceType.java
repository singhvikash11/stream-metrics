package bike.rapido.metrics.source.config.models;

import com.google.gson.annotations.SerializedName;

import static bike.rapido.metrics.utils.Constants.STREAM_SOURCE_DETAILS_SOURCE_TYPE_BOUNDED;
import static bike.rapido.metrics.utils.Constants.STREAM_SOURCE_DETAILS_SOURCE_TYPE_UNBOUNDED;

public enum SourceType {
    @SerializedName(STREAM_SOURCE_DETAILS_SOURCE_TYPE_BOUNDED)
    BOUNDED,
    @SerializedName(STREAM_SOURCE_DETAILS_SOURCE_TYPE_UNBOUNDED)
    UNBOUNDED
}
