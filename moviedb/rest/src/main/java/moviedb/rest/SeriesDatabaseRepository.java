package moviedb.rest;

import java.io.IOException;
import moviedb.core.LocalDatabaseSaver;
import moviedb.core.RemoteDatabaseSaver;
import moviedb.core.SeriesDatabase;
import moviedb.json.JsonFileHandler;
import org.springframework.stereotype.Repository;

@Repository
public class SeriesDatabaseRepository {

    private JsonFileHandler jsonFileHandler;
    private SeriesDatabase seriesDatabase;
    private static final String baseURI = "http://localhost:8080/moviedb";

    /**
     * Initializes a series repository by getting saved series from file and
     * adding the correct DatabaseObservers.
     */
    public SeriesDatabaseRepository() {
        jsonFileHandler = new JsonFileHandler(LocalDatabaseSaver.findPath("series.json"));
        try {
            seriesDatabase = jsonFileHandler.readSeriesDatabase();
        } catch (IOException ex) {
            seriesDatabase = new SeriesDatabase();
        }
        seriesDatabase.addObserver(new RemoteDatabaseSaver(baseURI));
        seriesDatabase.addObserver(new LocalDatabaseSaver());
    }

    public SeriesDatabase getSeries() {
        return this.seriesDatabase;
    }

    public void setSeries(SeriesDatabase series) {
        seriesDatabase = series;
    }

}
