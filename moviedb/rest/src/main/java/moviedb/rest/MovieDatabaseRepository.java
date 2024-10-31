package moviedb.rest;

import java.io.IOException;
import moviedb.core.LocalDatabaseSaver;
import moviedb.core.MovieDatabase;
import moviedb.core.RemoteDatabaseSaver;
import moviedb.json.JsonFileHandler;
import org.springframework.stereotype.Repository;

@Repository
public class MovieDatabaseRepository {

    private JsonFileHandler jsonFileHandler;
    private MovieDatabase movieDatabase;
    private static final String baseURI = "http://localhost:8080/moviedb";

    /**
     * Initializes a movie repository by getting saved movies from file and
     * adding the correct DatabaseObservers.
     */
    public MovieDatabaseRepository() {
        jsonFileHandler = new JsonFileHandler(LocalDatabaseSaver.findPath("movies.json"));
        try {
            movieDatabase = jsonFileHandler.readMovieDatabase();
        } catch (IOException ex) {
            movieDatabase = new MovieDatabase();
        }
        movieDatabase.addObserver(new RemoteDatabaseSaver(baseURI));
        movieDatabase.addObserver(new LocalDatabaseSaver());
    }

    public MovieDatabase getMovies() {
        return this.movieDatabase;
    }

    public void setMovies(MovieDatabase movies) {
        movieDatabase = movies;
    }

}
