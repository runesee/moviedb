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
import moviedb.core.Series;
import moviedb.core.SeriesDatabase;

public class SeriesDatabaseDeserializer extends JsonDeserializer<SeriesDatabase> {

    private SeriesDeserializer seriesDeserializer = new SeriesDeserializer();

    @Override
    public SeriesDatabase deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JacksonException {
        TreeNode treeNode = parser.getCodec().readTree(parser);
        return deserialize((JsonNode) treeNode);
    }

    SeriesDatabase deserialize(JsonNode treeNode) {
        if (treeNode instanceof ObjectNode objectNode) {
            SeriesDatabase seriesDatabase = new SeriesDatabase();
            JsonNode seriesNode = objectNode.get("series");
            if (seriesNode instanceof ArrayNode) {
                for (JsonNode elementNode : (ArrayNode) seriesNode) {
                    Series series = seriesDeserializer.deserialize(elementNode);
                    if (series != null) {
                        seriesDatabase.addSeriesUnchecked(series);
                    }
                }
            }
            return seriesDatabase;
        }
        return null;
    }

}
