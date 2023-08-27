package bike.rapido.metrics.serde.json.deserialization;


import bike.rapido.metrics.exception.serde.MetricDeserializationException;
import bike.rapido.metrics.serde.MetricDeserializer;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.formats.raw.RawFormatDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import static bike.rapido.metrics.core.Constants.ROWTIME;

public class JsonDeserializer implements KafkaDeserializationSchema<Row>, MetricDeserializer<Row> {
    private final JsonRowDeserializationSchema jsonRowDeserializationSchema;
    private final int rowtimeIdx;

    RawFormatDeserializationSchema temp;

    private final TypeInformation<Row> typeInformation;

    @Override
    public void open(DeserializationSchema.InitializationContext context) throws Exception {
        this.jsonRowDeserializationSchema.open(context);
    }

    public JsonDeserializer(String jsonSchema, String rowtimeFieldName) {
        this.typeInformation = new JsonType(jsonSchema, ROWTIME).getRowType();
        this.jsonRowDeserializationSchema = new JsonRowDeserializationSchema(typeInformation);
        RowTypeInfo rowTypeInfo = (RowTypeInfo) typeInformation;
        this.rowtimeIdx = rowTypeInfo.getFieldIndex(rowtimeFieldName);
    }

    @Override
    public boolean isEndOfStream(Row nextElement) {
        return false;
    }

    @Override
    public Row deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) {
        try {
         /*   String json = new String(consumerRecord.value());
            String flattenedJson = new JsonFlattener(json).withSeparator('-').flatten();*/
            Row inputRow = jsonRowDeserializationSchema.deserialize(consumerRecord.value());
            return addTimestampFieldToRow(inputRow);
        } catch (RuntimeException | IOException e) {
            throw new MetricDeserializationException(e);
        }
    }

    @Override
    public TypeInformation<Row> getProducedType() {
        return jsonRowDeserializationSchema.getProducedType();
    }

    private Row addTimestampFieldToRow(Row row) {
        Row finalRecord = new Row(row.getArity());

        for (int i = 0; i < row.getArity() - 2; i++) {
            finalRecord.setField(i, row.getField(i));
        }

        Object rowtimeField = row.getFieldAs(rowtimeIdx);
        if (rowtimeField instanceof BigDecimal) {
            BigDecimal bigDecimalField = (BigDecimal) row.getField(rowtimeIdx);
            finalRecord.setField(finalRecord.getArity() - 1, Timestamp.from(Instant.ofEpochSecond(bigDecimalField.longValue())));
        } else if (rowtimeField instanceof Timestamp) {
            finalRecord.setField(finalRecord.getArity() - 1, rowtimeField);
        } else {
            throw new MetricDeserializationException("Invalid Rowtime datatype for rowtimeField");
        }
        finalRecord.setField(finalRecord.getArity() - 2, true);

        return finalRecord;
    }
}
