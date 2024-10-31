package moviedb.core;

import java.util.List;

public class Movie extends AbstractMedia {
    private int runtime;

    /**
     * Constructor for the movie class.
     * 
     * @param name        name of the movie
     * @param releaseYear release year of the movie
     * @param genres      genres of the movie
     * @param runtime     runtime of the movie
     * @param rating      rating of the movie
     */
    public Movie(String name, int releaseYear, List<String> genres, int runtime, double rating) {
        super(name, releaseYear, genres, rating);
        setRuntime(runtime);

    }

    public Movie() {
        super();
    }

    public int getRuntime() {
        return runtime;
    }

    /**
     * Method for seting the runtime of the movie.
     * 
     * @param runtime runtime of the movie
     */
    public void setRuntime(int runtime) {
        if (runtime >= 1) {
            this.runtime = runtime;
        } else {
            throw new IllegalArgumentException("Runtime must be greater than 1 minute");
        }
    }

    @Override
    public String toString() {
        return "Name: " + name + " - "
                + "Release year: " + releaseYear + " - "
                + "Genre(s): " + genreToString() + " - "
                + "Runtime: " + runtime + " - "
                + "Rating: " + rating;
    }

}
