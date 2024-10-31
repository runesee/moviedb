package moviedb.core;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import moviedb.json.JsonFileHandler;

public class LocalDatabaseSaver implements DatabaseObserver {

    public static final String sep = FileSystems.getDefault().getSeparator();
    public static final String imageDir = System.getProperty("user.home") + sep + "moviedbfx" + sep + "media";
    public static final String imageDirWithSeparator = imageDir + sep;
    public static final String persistenceDir = System.getProperty("user.home") + sep + "moviedbfx" + sep
            + "persistence";

    @Override
    public void databaseChanged(AbstractObservableDatabase database) {
        String jsonFile;
        if (database instanceof MovieDatabase) {
            jsonFile = "movies.json";
        } else {
            jsonFile = "series.json";
        }
        Path path = findPath(jsonFile);
        saveDatabase(database, path);
    }

    public static Path findPath(String jsonFile) {
        String filepath = persistenceDir + sep + jsonFile;
        return Paths.get(filepath).toAbsolutePath();
    }

    /**
     * Method for saving the database.
     */
    public static void saveDatabase(AbstractObservableDatabase database, Path path) {
        JsonFileHandler json = new JsonFileHandler(path);
        try {
            json.writeDatabase(database);
        } catch (IOException e) {
            throw new RuntimeException("An error occured when saving the database!");
        }
    }

    /**
     * Method used for clearing the database json-file after tests.
     * 
     * @param jsonFile the json file for saving
     */
    public static void clearDatabase(String jsonFile) {
        Path path = findPath(jsonFile);
        if (jsonFile.equals("movies.json")) {
            saveDatabase(new MovieDatabase(), path);
        } else {
            saveDatabase(new SeriesDatabase(), path);
        }
    }

}
