import com.google.gson.*
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Iso8601OffsetDateTimeAdapter : JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override fun serialize(src: OffsetDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): OffsetDateTime {
        return OffsetDateTime.parse(json!!.asString, formatter)
    }
}
