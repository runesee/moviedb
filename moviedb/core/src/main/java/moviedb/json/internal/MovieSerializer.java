package moviedb.json.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import moviedb.core.Movie;

public class MovieSerializer extends JsonSerializer<Movie> {

    @Override
    public void serialize(Movie movie, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", movie.getName());
        gen.writeNumberField("releaseYear", movie.getReleaseYear());
        gen.writeArrayFieldStart("genres");
        for (String genre : movie.getGenres()) {
            gen.writeObject(genre);
        }
        gen.writeEndArray();
        gen.writeNumberField("runtime", movie.getRuntime());
        gen.writeNumberField("rating", movie.getRating());
        gen.writeEndObject();

    }

}
