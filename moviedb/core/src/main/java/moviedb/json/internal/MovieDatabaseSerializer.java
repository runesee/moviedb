package moviedb.json.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import moviedb.core.Movie;
import moviedb.core.MovieDatabase;

public class MovieDatabaseSerializer extends JsonSerializer<MovieDatabase> {

    @Override
    public void serialize(MovieDatabase database, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        if (database.getMovies().size() != 0) {
            gen.writeArrayFieldStart("movies");
            for (Movie movie : database) {
                gen.writeObject(movie);
            }
            gen.writeEndArray();
        }
    }

}
