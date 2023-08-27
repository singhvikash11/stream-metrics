package bike.rapido.metrics.serde.json.deserialization;

import bike.rapido.metrics.serde.MetricInternalTypeInformation;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.json.JsonRowSchemaConverter;
import org.apache.flink.types.Row;

import java.io.Serializable;

public class JsonType implements Serializable, MetricInternalTypeInformation {
    private String jsonSchema;
    private String rowtimeAttributeName;

    public JsonType(String jsonSchema, String rowtimeAttributeName) {
        this.jsonSchema = jsonSchema;
        this.rowtimeAttributeName = rowtimeAttributeName;
    }

    public TypeInformation<Row> getRowType() {
        TypeInformation<Row> rowNamed = JsonRowSchemaConverter.convert(jsonSchema);

        return addInternalFields(rowNamed, rowtimeAttributeName);
    }
}
