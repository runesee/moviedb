package moviedb.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MovieTest {

    @Test
    @DisplayName("Checking that constructor assigns correct values")
    public void testConstructor() {
        Movie movie = new Movie("Test", 1990, List.of("Genre"), 100, 5.0);
        assertEquals("Test", movie.getName());
        assertEquals(1990, movie.getReleaseYear());
        assertEquals(100, movie.getRuntime());
        assertEquals(5.0, movie.getRating());
        List<String> list = movie.getGenres();
        assertEquals("Genre", list.get(0));
        Movie movie2 = new Movie("Test", 1990, List.of("Genre", "Test"), 100, 5.0);
        list = movie2.getGenres();
        assertEquals("Test", list.get(1));
    }

    @Test
    @DisplayName("Checking that number values are withing threshold (validation)")
    public void testThresholdValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Test", -1, List.of("Genre"), 100, 5.0);
        }, "Constructor should not allow negative values for release year");
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Test", 1990, List.of("Genre"), 0, 5.0);
        }, "Constructor should not allow a runtime of zero");
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Test", 1990, List.of("Genre"), -1, 5.0);
        }, "Constructor should not allow negative runtime");
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Test", 1990, List.of("Genre"), 100, 0.3);
        }, "Constructor should not allow ratings less than 1.0");
        assertThrows(IllegalArgumentException.class, () -> {
            new Movie("Test", 1990, List.of("Genre"), -1, 10.1);
        }, "Constructor should not allow rating higher than 10.0");
    }

    @Test
    @DisplayName("Test coverage for setters")
    public void testSetters() {
        Movie movie = new Movie("Test", 1990, List.of("Genre"), 100, 5.0);
        movie.setRating(6.0);
        assertEquals(6.0, movie.getRating());
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setRating(0.3);
        }, "Setter should not allow rating higher than 10.0");
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setRating(10.1);
        }, "Setter should not allow rating higher than 10.0");
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setReleaseYear(1700);
        }, "Setter should only allow release years between 1800 and 2022");
        assertThrows(IllegalArgumentException.class, () -> {
            movie.setReleaseYear(2025);
        }, "Setter should only allow release years between 1800 and 2022");
        movie.setReleaseYear(2010);
        assertEquals(2010, movie.getReleaseYear());
    }

    @Test
    @DisplayName("Tostring() test")
    public void testToString() {
        Movie movie = new Movie("Test", 1990, List.of("Genre"), 100, 5.0);
        Movie movie2 = new Movie("Test2", 1990, List.of("Genre, Genre2, Genre3"), 100, 5.0);
        assertEquals("Name: Test - Release year: 1990 - Genre(s): Genre - Runtime: 100 - Rating: 5.0",
                movie.toString());
        assertEquals("Name: Test2 - Release year: 1990 - Genre(s): Genre, Genre2, Genre3 - Runtime: 100 - Rating: 5.0",
                movie2.toString());
    }

    @Test
    public void testGenreToString() {
        Movie movie = new Movie("Test", 1990, List.of("Genre"), 100, 5.0);
        Movie movie2 = new Movie("Test", 1990, List.of("Genre, Genre2"), 100, 5.0);
        Movie movie3 = new Movie("Test", 1990, List.of("Genre, Genre2"), 100, 5.0);
        assertEquals("Genre", movie.genreToString());
        assertEquals("Genre, Genre2", movie2.genreToString());
        assertNotEquals("Genre, Genre2, Genre3,", movie3.genreToString());
    }

    @Test
    public void testAddGenre() {
        Movie movie = new Movie("Test", 1990, List.of("Genre"), 100, 5.0);
        assertEquals(List.of("Genre"), movie.getGenres());
        Movie movie2 = new Movie();
        assertEquals(List.of(), movie2.getGenres());
        movie2.addGenre("Test");
        assertEquals(List.of("Test"), movie2.getGenres());
    }

}