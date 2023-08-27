package bike.rapido.metrics.source;


import bike.rapido.metrics.config.Configuration;
import bike.rapido.metrics.source.config.StreamConfig;

import java.util.ArrayList;
import java.util.List;

public class StreamsFactory {
    public static List<Stream> getStreams(Configuration configuration) {
        StreamConfig[] streamConfigs = StreamConfig.parse(configuration);
        ArrayList<Stream> streams = new ArrayList<>();

        for (StreamConfig streamConfig : streamConfigs) {
            Stream.Builder builder = new Stream.Builder(streamConfig, configuration);
            streams.add(builder.build());
        }
        return streams;
    }
}
