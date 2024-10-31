package moviedb.json.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import moviedb.core.Series;

public class SeriesSerializer extends JsonSerializer<Series> {

    @Override
    public void serialize(Series series, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", series.getName());
        gen.writeNumberField("releaseYear", series.getReleaseYear());
        gen.writeArrayFieldStart("genres");
        for (String genre : series.getGenres()) {
            gen.writeObject(genre);
        }
        gen.writeEndArray();
        gen.writeNumberField("seasons", series.getSeasons());
        gen.writeNumberField("rating", series.getRating());
        gen.writeEndObject();

    }

}