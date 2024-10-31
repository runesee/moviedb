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
import moviedb.core.Series;

public class SeriesDeserializer extends JsonDeserializer<Series> {

    @Override
    public Series deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode treeNode = parser.getCodec().readTree(parser);
        return deserialize((JsonNode) treeNode);
    }

    Series deserialize(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode objectNode) {
            Series series = new Series();
            JsonNode textNode = objectNode.get("name");
            if (textNode instanceof TextNode) {
                series.setName(textNode.asText());
            }
            JsonNode releaseYearNode = objectNode.get("releaseYear");
            if (releaseYearNode instanceof IntNode) {
                series.setReleaseYear(releaseYearNode.asInt());
            }
            JsonNode genreNode = objectNode.get("genres");
            if (genreNode instanceof ArrayNode) {
                for (JsonNode elementNode : (ArrayNode) genreNode) {
                    String genre = elementNode.asText();
                    if (genre != null) {
                        series.addGenre(genre);
                    }
                }
            }
            JsonNode seasonsNode = objectNode.get("seasons");
            if (seasonsNode instanceof IntNode) {
                series.setSeasons(seasonsNode.asInt());
            }
            JsonNode ratingNode = objectNode.get("rating");
            if (ratingNode instanceof DoubleNode) {
                series.setRating(ratingNode.asDouble());
            }
            return series;
        }
        return null;
    }

}
