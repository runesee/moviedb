package moviedb.core;

import java.util.List;

public class Series extends AbstractMedia {
    private int seasons;

    public Series(String name, int releaseYear, List<String> genres, int seasons, double rating) {
        super(name, releaseYear, genres, rating);
        setSeasons(seasons);
    }

    public Series() {
        super();
    }

    public int getSeasons() {
        return seasons;
    }

    /**
     * Method for seting number of seasons of a series.
     * 
     * @param seasons the number of seasons
     */
    public void setSeasons(int seasons) {
        if (seasons > 0) {
            this.seasons = seasons;
        } else {
            throw new IllegalArgumentException("Series must have at least one season!");
        }
    }

    @Override
    public String toString() {
        return "Name: " + name + " - "
                + "Release year: " + releaseYear + " - "
                + "Genre(s): " + genreToString() + " - "
                + "Seasons: " + seasons + " - "
                + "Rating: " + rating;
    }
}
