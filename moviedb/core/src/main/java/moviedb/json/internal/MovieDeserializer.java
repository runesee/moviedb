package moviedb.json.internal;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import moviedb.core.Movie;

public class MovieDeserializer extends JsonDeserializer<Movie> {

    @Override
    public Movie deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode treeNode = parser.getCodec().readTree(parser);
        return deserialize((JsonNode) treeNode);
    }

    Movie deserialize(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode objectNode) {
            Movie movie = new Movie();
            JsonNode textNode = objectNode.get("name");
            if (textNode instanceof TextNode) {
                movie.setName(textNode.asText());
            }
            JsonNode releaseYearNode = objectNode.get("releaseYear");
            if (releaseYearNode instanceof IntNode) {
                movie.setReleaseYear(releaseYearNode.asInt());
            }
            JsonNode genreNode = objectNode.get("genres");
            if (genreNode instanceof ArrayNode) {
                for (JsonNode elementNode : (ArrayNode) genreNode) {
                    String genre = elementNode.asText();
                    if (genre != null) {
                        movie.addGenre(genre);
                    }
                }
            }
            JsonNode runtimeNode = objectNode.get("runtime");
            if (runtimeNode instanceof IntNode) {
                movie.setRuntime(runtimeNode.asInt());
            }
            JsonNode ratingNode = objectNode.get("rating");
            if (ratingNode instanceof DoubleNode) {
                movie.setRating(ratingNode.asDouble());
            }
            return movie;
        }
        return null;
    }

}
