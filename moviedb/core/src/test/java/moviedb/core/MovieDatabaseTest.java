package moviedb.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import moviedb.json.JsonFileHandler;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDatabaseTest {

    private MovieDatabase db = new MovieDatabase();
    List<Movie> movies = new ArrayList<>();

    @BeforeEach
    public void initialize() {
        // Clear all movies from text file to allow for proper testing:
        JsonFileHandler jfh = new JsonFileHandler(LocalDatabaseSaver.findPath("movies.json"));
        try {
            db = jfh.readMovieDatabase();
            db.addObserver(new LocalDatabaseSaver());
        } catch (IOException e) {
            // do nothing
        }
        for (Movie movie : db.getMovies()) {
            movies.add(movie);
        }
        for (Movie movie : movies) {
            db.removeMovie(movie);
        }
    }

    @Test
    @DisplayName("Test for addMovie() and removeMovie() methods")
    public void testAddAndRemoveMovie() {
        Movie movie = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);
        Movie movie2 = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);
        db.addMovie(movie);

        // Test that addMovie gives expected values:
        assertEquals("Test", db.getMovies().get(0).getName());
        assertEquals(1990, db.getMovies().get(0).getReleaseYear());
        assertEquals("Genre", db.getMovies().get(0).getGenres().get(0));
        assertEquals(100, db.getMovies().get(0).getRuntime());
        assertEquals(5.0, db.getMovies().get(0).getRating());

        // Asserts that movie both in database and savefile cannot be added:
        assertFalse(db.addMovie(movie), "Cannot add the same movie twice");

        // Asserts that the same movie cannot be added twice using contains():
        assertFalse(db.addMovie(movie2), "Cannot add the same movie twice");
        // Asserts that the same movie cannot be added twice using savefile:
        MovieDatabase db2 = new MovieDatabase();
        db2.addObserver(new LocalDatabaseSaver());
        assertFalse(db2.addMovie(movie2), "Cannot add the same movie twice");

        // Asserts that movie neither in database nor savefile can be added:
        db.removeMovie(movie);
        assertTrue(db2.addMovie(movie) && db2.removeMovie(movie));

        Movie movie2Clone1 = new Movie("NotTest", 1990, Arrays.asList("Genre"), 100,
                5.0);
        // Asserts that movies with different names always are unique and are therefore
        // added:
        assertTrue(db.addMovie(movie2Clone1) && db.removeMovie(movie2Clone1));

        // Asserts that movies with same name and release years, but different runtimes
        // are added:
        Movie movie2Clone2 = new Movie("Test", 1990, Arrays.asList("Genre"), 200,
                5.0);
        assertTrue(db.addMovie(movie2Clone2) && db.removeMovie(movie2Clone2));

        // Asserts that movies with the same name and runtimes, but different release
        // years are added
        Movie movie2Clone3 = new Movie("Test", 2000, Arrays.asList("Genre"), 100,
                5.0);
        assertTrue(db.addMovie(movie2Clone3) && db.removeMovie(movie2Clone3));

        // Asserts that movies with the same name but different runtimes and release
        // years are added:
        Movie movie2Clone4 = new Movie("Test", 2000, Arrays.asList("Genre"), 200,
                7.0);
        assertTrue(db.addMovie(movie2Clone4) && db.removeMovie(movie2Clone4));

        // Asserts full coverage for removeMovie method:
        Movie movie3 = new Movie("Unique", 1990, Arrays.asList("Genre"), 100, 5.0);
        assertFalse(db2.getMovies().contains(movie3));
        db2.removeMovie(movie3);
        assertFalse(db2.getMovies().contains(movie3));

        // Resets savefile before ending:
        db.removeMovie(movie);
        finish();
    }

    @Test
    @DisplayName("Test for toString() method")
    public void testToString() {
        MovieDatabase database = new MovieDatabase();
        Movie movie = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);
        Movie movie2 = new Movie("Test2", 1998, Arrays.asList("Genre, Genre2"), 200, 7.0);
        assertEquals("", database.toString());
        database.addMovie(movie);
        assertEquals("Test", database.toString());
        database.addMovie(movie2);
        assertEquals("Test, Test2", database.toString());

        // Resets savefile before ending:
        database.removeMovie(movie);
        database.removeMovie(movie2);
        finish();
    }

    @Test
    public void testSetMovies() {
        MovieDatabase database = new MovieDatabase();
        Movie movie = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);
        Movie movie2 = new Movie("Test2", 1998, Arrays.asList("Genre, Genre2"), 200, 7.0);
        database.setMovies(Arrays.asList(movie, movie2));
        assertEquals(Arrays.asList(movie, movie2), database.getMovies());
    }

    @Test
    public void testAddMovieUnchecked() {
        Movie movie = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);
        db.addMovieUnchecked(movie);
        assertEquals(Arrays.asList(movie), db.getMovies());

        // Resets savefile before ending:
        db.removeMovie(movie);
        finish();
    }

    @Test
    public void testDeserialize() {
        Movie movie = new Movie("Test", 1990, Arrays.asList("Genre"), 100, 5.0);

        JsonFileHandler json = new JsonFileHandler(LocalDatabaseSaver.findPath("movies.json"));
        MovieDatabase db2 = null;
        try {
            db2 = json.readMovieDatabase();
        } catch (IOException e) {
            fail();
        }
        assertEquals(Arrays.asList(), db2.getMovies());

        try {
            db2 = json.readMovieDatabase();
        } catch (IOException e) {
            fail();
        }
        for (Movie m : db2.getMovies()) {
            assertEquals("Test", m.getName());
            assertEquals(1990, m.getReleaseYear());
            assertEquals(Arrays.asList("Genre"), m.getGenres());
            assertEquals(100, m.getRuntime());
            assertEquals(5.0, m.getRating());
        }
        db2.addMovie(movie);
        for (Movie m : db2.getMovies()) {
            assertEquals("Test", m.getName());
            assertEquals(1990, m.getReleaseYear());
            assertEquals(Arrays.asList("Genre"), m.getGenres());
            assertEquals(100, m.getRuntime());
            assertEquals(5.0, m.getRating());
        }

        // Resets savefile before ending:
        db2.removeMovie(movie);
        finish();
    }

    private void finish() {
        // Writes original files back to savefile:
        for (Movie m : movies) {
            try {
                db.addMovie(m);
            } catch (Exception ignored) {
            }
        }
    }

}
