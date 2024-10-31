package moviedb.json.internal;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import moviedb.core.Movie;
import moviedb.core.MovieDatabase;

public class MovieModule extends SimpleModule {

    private static final String NAME = "MovieModule";

    /**
     * Creates a new MovieModule, and adds serializer and deserializer for both
     * Movie and MovieDatabase.
     */
    public MovieModule() {
        super(NAME, Version.unknownVersion());
        addSerializer(Movie.class, new MovieSerializer());
        addDeserializer(Movie.class, new MovieDeserializer());

        addSerializer(MovieDatabase.class, new MovieDatabaseSerializer());
        addDeserializer(MovieDatabase.class, new MovieDatabaseDeserializer());
    }

}
