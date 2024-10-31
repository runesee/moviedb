package moviedb.json.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import moviedb.core.Series;
import moviedb.core.SeriesDatabase;

public class SeriesDatabaseSerializer extends JsonSerializer<SeriesDatabase> {

    @Override
    public void serialize(SeriesDatabase database, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        if (database.getSeries().size() != 0) {
            gen.writeArrayFieldStart("series");
            for (Series series : database) {
                gen.writeObject(series);
            }
            gen.writeEndArray();
        }
    }

}
