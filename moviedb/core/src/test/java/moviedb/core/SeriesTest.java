package moviedb.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SeriesTest {

    @Test
    @DisplayName("Checking that constructor assigns correct values")
    public void testConstructor() {
        Series series = new Series("Test", 1990, List.of("Genre"), 10, 5.0);
        assertEquals("Test", series.getName());
        assertEquals(1990, series.getReleaseYear());
        assertEquals(10, series.getSeasons());
        assertEquals(5.0, series.getRating());
        List<String> list = series.getGenres();
        assertEquals("Genre", list.get(0));
        Series series2 = new Series("Test", 1990, List.of("Genre", "Test"), 10, 5.0);
        list = series2.getGenres();
        assertEquals("Test", list.get(1));
    }

    @Test
    @DisplayName("Checking that number values are withing threshold (validation)")
    public void testThresholdValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Series("Test", -1, List.of("Genre"), 10, 5.0);
        }, "Constructor should not allow negative values for release year");
        assertThrows(IllegalArgumentException.class, () -> {
            new Series("Test", 1990, List.of("Genre"), 0, 5.0);
        }, "Constructor should not allow number of seasons as zero");
        assertThrows(IllegalArgumentException.class, () -> {
            new Series("Test", 1990, List.of("Genre"), -1, 5.0);
        }, "Constructor should not allow negative number of seasons");
        assertThrows(IllegalArgumentException.class, () -> {
            new Series("Test", 1990, List.of("Genre"), 10, 0.3);
        }, "Constructor should not allow ratings less than 1.0");
        assertThrows(IllegalArgumentException.class, () -> {
            new Series("Test", 1990, List.of("Genre"), -1, 10.1);
        }, "Constructor should not allow rating higher than 10.0");
    }

    @Test
    @DisplayName("Test coverage for setters")
    public void testSetters() {
        Series series = new Series("Test", 1990, List.of("Genre"), 10, 5.0);
        series.setRating(6.0);
        assertEquals(6.0, series.getRating());
        assertThrows(IllegalArgumentException.class, () -> {
            series.setRating(0.3);
        }, "Setter should not allow rating higher than 10.0");
        assertThrows(IllegalArgumentException.class, () -> {
            series.setRating(10.1);
        }, "Setter should not allow rating higher than 10.0");
        assertThrows(IllegalArgumentException.class, () -> {
            series.setReleaseYear(1700);
        }, "Setter should only allow release years between 1800 and 2022");
        assertThrows(IllegalArgumentException.class, () -> {
            series.setReleaseYear(2025);
        }, "Setter should only allow release years between 1800 and 2022");
        series.setReleaseYear(2010);
        assertEquals(2010, series.getReleaseYear());
    }

    @Test
    @DisplayName("Tostring() test")
    public void testToString() {
        Series series = new Series("Test", 1990, List.of("Genre"), 10, 5.0);
        Series series2 = new Series("Test2", 1990, List.of("Genre, Genre2, Genre3"), 10, 5.0);
        assertEquals("Name: Test - Release year: 1990 - Genre(s): Genre - Seasons: 10 - Rating: 5.0",
                series.toString());
        assertEquals("Name: Test2 - Release year: 1990 - Genre(s): Genre, Genre2, Genre3 - Seasons: 10 - Rating: 5.0",
                series2.toString());
    }

    @Test
    public void testGenreToString() {
        Series series = new Series("Test", 1990, List.of("Genre"), 10, 5.0);
        Series series2 = new Series("Test", 1990, List.of("Genre, Genre2"), 10, 5.0);
        Series series3 = new Series("Test", 1990, List.of("Genre, Genre2"), 10, 5.0);
        assertEquals("Genre", series.genreToString());
        assertEquals("Genre, Genre2", series2.genreToString());
        assertNotEquals("Genre, Genre2, Genre3,", series3.genreToString());
    }

    @Test
    public void testAddGenre() {
        Series series = new Series("Test", 1990, List.of("Genre"), 10, 5.0);
        assertEquals(List.of("Genre"), series.getGenres());
        Series series2 = new Series();
        assertEquals(List.of(), series2.getGenres());
        series2.addGenre("Test");
        assertEquals(List.of("Test"), series2.getGenres());
    }

}