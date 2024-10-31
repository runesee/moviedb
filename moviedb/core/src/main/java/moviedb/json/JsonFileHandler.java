package moviedb.json;

import static moviedb.core.LocalDatabaseSaver.persistenceDir;
import static moviedb.core.LocalDatabaseSaver.sep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import moviedb.core.AbstractObservableDatabase;
import moviedb.core.LocalDatabaseSaver;
import moviedb.core.MovieDatabase;
import moviedb.core.SeriesDatabase;
import moviedb.json.internal.MovieModule;
import moviedb.json.internal.SeriesModule;

public class JsonFileHandler {

    private final File file;
    private final ObjectMapper mapper;

    /**
     * Creates a new JsonFileHandler with the given path.
     * Sets the mapper to a MovieObjectMapper if path ends with "movies.json", sets
     * it to
     * a SeriesObjectMapper if not.
     *
     * @param path the path to the persistence file
     */
    public JsonFileHandler(Path path) {
        initializeFiles();
        String[] pathArray = path.toString().split(Pattern.quote(System.getProperty("file.separator")));
        if (pathArray[pathArray.length - 1].equals("movies.json")) {
            mapper = createMovieObjectMapper();
        } else {
            mapper = createSeriesObjectMapper();
        }
        file = new File(path.toString());
    }

    public static ObjectMapper createSeriesObjectMapper() {
        SimpleModule module = createSeriesJacksonModule();
        return new ObjectMapper().registerModule(module);
    }

    public static SimpleModule createSeriesJacksonModule() {
        return new SeriesModule();
    }

    public static SimpleModule createMovieJacksonModule() {
        return new MovieModule();
    }

    public static ObjectMapper createMovieObjectMapper() {
        SimpleModule module = createMovieJacksonModule();
        return new ObjectMapper().registerModule(module);
    }

    /**
     * Reads a MovieDatabase from the file set in the JsonFileHandler.
     *
     * @return MovieDatabase the database read from file
     * @throws IOException if file is not found
     */
    public MovieDatabase readMovieDatabase() throws IOException {
        try {
            return mapper.readValue(file, MovieDatabase.class);
        } catch (MismatchedInputException e) { // json-file is empty, return empty movieDatabase-object
            return new MovieDatabase();
        }
    }

    /**
     * Reads a MovieDatabase from the file set in the JsonFileHandler.
     *
     * @return SeriesDatabase the database read from file
     * @throws IOException if file is not found
     */
    public SeriesDatabase readSeriesDatabase() throws IOException {
        try {
            return mapper.readValue(file, SeriesDatabase.class);
        } catch (MismatchedInputException e) { // json-file is empty, return empty seriesDatabase-object
            return new SeriesDatabase();
        }
    }

    /**
     * Writes the database given as a parameter to the file set in the
     * JsonFileHandler.
     *
     * @param database an AbstractObservableDatabase-object
     * @throws IOException if path is illegal
     */
    public void writeDatabase(AbstractObservableDatabase database) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, database);
    }

    // checks if the persistence files already exist, and makes the directory and
    // the files if not
    private void initializeFiles() {
        boolean created;
        File moviesFile = new File(persistenceDir + sep + "movies.json");
        File seriesFile = new File(persistenceDir + sep + "series.json");
        if (!(moviesFile.isFile() && seriesFile.isFile())) {
            File persistenceDir = new File(LocalDatabaseSaver.persistenceDir);
            created = persistenceDir.mkdirs(); // makes the folders
            if (created) {
                boolean moviesCreated;
                boolean seriesCreated;
                try {
                    // creates empty json files
                    moviesCreated = moviesFile.createNewFile();
                    seriesCreated = seriesFile.createNewFile();
                    if (!moviesCreated || !seriesCreated) {
                        throw new RuntimeException("Could not create persistence files!");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } else {
                throw new RuntimeException("Could not create directory for persistence files!");
            }
        }
    }

}
