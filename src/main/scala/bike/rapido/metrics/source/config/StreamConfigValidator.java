package bike.rapido.metrics.source.config;


import bike.rapido.metrics.source.config.models.SourceDetails;
import bike.rapido.metrics.source.config.models.SourceName;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.stream.Stream;

import static bike.rapido.metrics.utils.Constants.*;


public class StreamConfigValidator {
    public static StreamConfig validateSourceDetails(StreamConfig streamConfig) {
        SourceDetails[] sourceDetailsArray = streamConfig.getSourceDetails();
        Preconditions.checkArgument(sourceDetailsArray.length != 0, "%s config is set to "
                        + "an empty array. Please check the documentation and specify in a valid format.",
                STREAM_SOURCE_DETAILS_KEY);
        for (SourceDetails sourceDetails : sourceDetailsArray) {
            Preconditions.checkArgument(sourceDetails != null, "One or more elements inside %s "
                    + "is either null or invalid.", STREAM_SOURCE_DETAILS_KEY);
            Preconditions.checkArgument(sourceDetails.getSourceName() != null, "One or more "
                    + "elements inside %s has null or invalid SourceName. Check if it is a valid SourceName and ensure "
                    + "no trailing/leading whitespaces are present", STREAM_SOURCE_DETAILS_KEY);
            Preconditions.checkArgument(sourceDetails.getSourceType() != null, "One or more "
                    + "elements inside %s has null or invalid SourceType. Check if it is a valid SourceType and ensure "
                    + "no trailing/leading whitespaces are present", STREAM_SOURCE_DETAILS_KEY);
        }
        return streamConfig;
    }

}
