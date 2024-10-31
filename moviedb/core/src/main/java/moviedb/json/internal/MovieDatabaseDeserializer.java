package moviedb.json.internal;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import moviedb.core.Movie;
import moviedb.core.MovieDatabase;

public class MovieDatabaseDeserializer extends JsonDeserializer<MovieDatabase> {

    private MovieDeserializer movieDeserializer = new MovieDeserializer();

    @Override
    public MovieDatabase deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JacksonException {
        TreeNode treeNode = parser.getCodec().readTree(parser);
        return deserialize((JsonNode) treeNode);
    }

    MovieDatabase deserialize(JsonNode treeNode) {
        if (treeNode instanceof ObjectNode objectNode) {
            MovieDatabase movieDatabase = new MovieDatabase();
            JsonNode moviesNode = objectNode.get("movies");
            if (moviesNode instanceof ArrayNode) {
                for (JsonNode elementNode : (ArrayNode) moviesNode) {
                    Movie movie = movieDeserializer.deserialize(elementNode);
                    if (movie != null) {
                        movieDatabase.addMovieUnchecked(movie);
                    }
                }
            }
            return movieDatabase;
        }
        return null;
    }

}
