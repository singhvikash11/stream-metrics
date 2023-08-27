package bike.rapido.metrics.serde;

import bike.rapido.metrics.core.Constants;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Arrays;

public interface MetricInternalTypeInformation {
    TypeInformation<Row> getRowType();

    default TypeInformation<Row> addInternalFields(TypeInformation<Row> initialTypeInfo, String rowtimeAttributeName) {
        RowTypeInfo rowTypeInfo = (RowTypeInfo) initialTypeInfo;
        ArrayList<String> fieldNames = new ArrayList<>(Arrays.asList(rowTypeInfo.getFieldNames()));
        ArrayList<TypeInformation> fieldTypes = new ArrayList<>(Arrays.asList(rowTypeInfo.getFieldTypes()));
        fieldNames.add(Constants.INTERNAL_VALIDATION_FIELD_KEY);
        fieldTypes.add(Types.BOOLEAN);
        fieldNames.add(rowtimeAttributeName);
        fieldTypes.add(Types.SQL_TIMESTAMP);
        return Types.ROW_NAMED(fieldNames.toArray(new String[0]), fieldTypes.toArray(new TypeInformation[0]));
    }
}
