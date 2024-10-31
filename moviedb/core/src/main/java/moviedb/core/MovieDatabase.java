package moviedb.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import moviedb.json.JsonFileHandler;

public class MovieDatabase extends AbstractObservableDatabase implements Iterable<Movie> {
    private List<Movie> movies;

    public MovieDatabase() {
        this.movies = new ArrayList<>(); // creates empty database if the json-file is empty
    }

    /**
     * Adds the given movie to the database, if not already saved.
     * 
     * @param movie a movieobject
     * @return boolean
     */
    public boolean addMovie(Movie movie) {
        if ((this.movies).contains(movie) || (movieAlreadySaved(movie))) {
            return false;
        } else {
            this.movies.add(movie);
            notifyObservers();
            return true;
        }
    }

    /**
     * Adds the given movie to the database, without checking if it is already
     * saved.
     * 
     * @param movie a movieobject
     */
    public void addMovieUnchecked(Movie movie) {
        // This method is only used when importing a MovieDatabase-object from a file,
        // and therefore doesn't need to check if the movie is already added
        this.movies.add(movie);
        notifyObservers();
    }

    /**
     * Removes the given movie from the database.
     * 
     * @param movie a movieobject
     * @return boolean
     */
    public boolean removeMovie(Movie movie) {
        if (this.movies.contains(movie)) {
            this.movies.remove(movie);
            notifyObservers();
            return true;
        }
        return false;
    }

    public List<Movie> getMovies() {
        return this.movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        for (DatabaseObserver observer : this.observers) {
            observer.databaseChanged(this);
        }
    }

    @Override
    public Iterator<Movie> iterator() {
        return movies.iterator();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Movie saveFileMovie : this.movies) {
            str.append(saveFileMovie.getName()).append(", ");
        }
        try {
            str.deleteCharAt(str.length() - 1);
            str.deleteCharAt(str.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
        return str.toString();
    }

    // Method to check if movie is already saved
    private boolean movieAlreadySaved(Movie movie) {
        boolean isLocal = false;
        boolean isRemote = false;
        RemoteDatabaseSaver remoteSaver = null;
        for (DatabaseObserver observer : observers) {
            if (observer instanceof LocalDatabaseSaver) {
                isLocal = true;
            } else if (observer instanceof RemoteDatabaseSaver) {
                isRemote = true;
                remoteSaver = (RemoteDatabaseSaver) observer;
            }
        }
        if (observers.size() != 0) {
            if (isLocal) {
                MovieDatabase savedDatabase = null;
                try {
                    savedDatabase = JsonFileHandler.createMovieObjectMapper()
                            .readValue(new File(LocalDatabaseSaver.findPath("movies.json").toString()),
                                    MovieDatabase.class);
                } catch (IOException e) {
                    // Exception is thrown when file is empty, has no impact reading from file
                    // overall
                }
                if (savedDatabase != null) {
                    if (savedDatabase.getMovies().size() == 0) {
                        return false;
                    }
                    for (Movie savedMovie : savedDatabase) {
                        if (savedMovie.getName().equals(movie.getName())) {
                            if (savedMovie.getReleaseYear() == movie.getReleaseYear()
                                    && savedMovie.getRuntime() == movie.getRuntime()) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
            if (isRemote) {
                try {
                    for (Movie remoteMovie : remoteSaver.getWebClient().getMovieDatabase()) {
                        if (remoteMovie.getName().equals(movie.getName())) {
                            if (remoteMovie.getReleaseYear() == movie.getReleaseYear()
                                    && remoteMovie.getRuntime() == movie.getRuntime()) {
                                return true;
                            }
                        }
                    }
                    return false;
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException("Could not check if movie is already saved!");
                }
            }
            return false;
        }
        return false;
    }

}
