package bike.rapido.metrics.sink.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import java.io.Serializable;

/**
 * The Influx db factory wrapper.
 */
public class InfluxDBFactoryWrapper implements Serializable {

    /**
     * Connect influx db.
     *
     * @param url     the url
     * @param orgName the orgName
     * @param bucket  the bucket
     * @param token   the token
     * @return the influx db
     */
    public InfluxDBClient connect(String url, String orgName, String bucket, String token) {
        InfluxDBClient client = null;
        try {
            client = InfluxDBClientFactory.create(url,
                    token.toCharArray(),
                    orgName, bucket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }
}
