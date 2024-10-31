package moviedb.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moviedb.json.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SeriesDatabaseTest {

    private SeriesDatabase db = new SeriesDatabase();
    List<Series> series = new ArrayList<>();

    @BeforeEach
    public void initialize() {
        // Clear all series from text file to allow for proper testing:
        JsonFileHandler jfh = new JsonFileHandler(LocalDatabaseSaver.findPath("series.json"));
        try {
            db = jfh.readSeriesDatabase();
            db.addObserver(new LocalDatabaseSaver());
        } catch (IOException e) {
            // do nothing
        }
        for (Series series : db.getSeries()) {
            this.series.add(series);
        }
        for (Series series : series) {
            db.removeSeries(series);
        }
    }

    @Test
    @DisplayName("Test for addSeries() and removeSeries() methods")
    public void testAddAndRemoveSeries() {
        Series series = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);
        Series series2 = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);
        db.addSeries(series);

        // Test that addSeries gives expected values:
        assertEquals("Test", db.getSeries().get(0).getName());
        assertEquals(1990, db.getSeries().get(0).getReleaseYear());
        assertEquals("Genre", db.getSeries().get(0).getGenres().get(0));
        assertEquals(10, db.getSeries().get(0).getSeasons());
        assertEquals(5.0, db.getSeries().get(0).getRating());

        // Asserts that series both in database and savefile cannot be added:
        assertFalse(db.addSeries(series), "Cannot add the same series twice");

        // Asserts that the same series cannot be added twice using contains():
        assertFalse(db.addSeries(series2), "Cannot add the same series twice");
        // Asserts that the same movie cannot be added twice using savefile:
        SeriesDatabase db2 = new SeriesDatabase();
        db2.addObserver(new LocalDatabaseSaver());
        assertFalse(db2.addSeries(series2), "Cannot add the same series twice");

        // Asserts that series neither in database nor savefile can be added:
        db.removeSeries(series);
        assertTrue(db2.addSeries(series) && db2.removeSeries(series));

        Series series2Clone1 = new Series("NotTest", 1990, Arrays.asList("Genre"), 10,
                5.0);
        // Asserts that series with different names always are unique and are therefore
        // added:
        assertDoesNotThrow(() -> {
            db.addSeries(series2Clone1);
            db.removeSeries(series2Clone1);
        });

        // Asserts that series with same name and release years, but different number of
        // seasonss
        // are added:
        Series series2Clone2 = new Series("Test", 1990, Arrays.asList("Genre"), 200,
                5.0);
        assertDoesNotThrow(() -> {
            db.addSeries(series2Clone2);
            db.removeSeries(series2Clone2);
        });

        // Asserts that seriess with the same name and number of seasons, but different
        // release
        // years are added
        Series series2Clone3 = new Series("Test", 2000, Arrays.asList("Genre"), 10,
                5.0);
        assertDoesNotThrow(() -> {
            db.addSeries(series2Clone3);
            db.removeSeries(series2Clone3);
        });

        // Asserts that series with the same name but different number of seasons and
        // release
        // years are added:
        Series series2Clone4 = new Series("Test", 2000, Arrays.asList("Genre"), 200,
                7.0);
        assertDoesNotThrow(() -> {
            db.addSeries(series2Clone4);
            db.removeSeries(series2Clone4);
        });

        // Asserts full coverage for removeSeries method:
        Series series3 = new Series("Unique", 1990, Arrays.asList("Genre"), 10, 5.0);
        assertFalse(db2.getSeries().contains(series3));
        db2.removeSeries(series3);
        assertFalse(db2.getSeries().contains(series3));

        // Resets savefile before ending:
        db.removeSeries(series);
        finish();
    }

    @Test
    @DisplayName("Test for toString() method")
    public void testToString() {
        SeriesDatabase database = new SeriesDatabase();
        Series series = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);
        Series series2 = new Series("Test2", 1998, Arrays.asList("Genre, Genre2"), 200, 7.0);
        assertEquals("", database.toString());
        database.addSeries(series);
        assertEquals("Test", database.toString());
        database.addSeries(series2);
        assertEquals("Test, Test2", database.toString());

        // Resets savefile before ending:
        database.removeSeries(series);
        database.removeSeries(series2);
        finish();
    }

    @Test
    public void testSetSeries() {
        SeriesDatabase database = new SeriesDatabase();
        Series series = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);
        Series series2 = new Series("Test2", 1998, Arrays.asList("Genre, Genre2"), 200, 7.0);
        database.setSeries(Arrays.asList(series, series2));
        assertEquals(Arrays.asList(series, series2), database.getSeries());
    }

    @Test
    public void testAddSeriesUnchecked() {
        Series series = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);
        db.addSeriesUnchecked(series);
        assertEquals(Arrays.asList(series), db.getSeries());

        // Resets savefile before ending:
        db.removeSeries(series);
        finish();
    }

    @Test
    public void testDeserialize() {
        Series series = new Series("Test", 1990, Arrays.asList("Genre"), 10, 5.0);

        JsonFileHandler json = new JsonFileHandler(LocalDatabaseSaver.findPath("series.json"));
        SeriesDatabase db2 = null;
        try {
            db2 = json.readSeriesDatabase();
        } catch (IOException e) {
            fail();
        }
        assertEquals(Arrays.asList(), db2.getSeries());

        try {
            db2 = json.readSeriesDatabase();
        } catch (IOException e) {
            fail();
        }
        for (Series m : db2.getSeries()) {
            assertEquals("Test", m.getName());
            assertEquals(1990, m.getReleaseYear());
            assertEquals(Arrays.asList("Genre"), m.getGenres());
            assertEquals(10, m.getSeasons());
            assertEquals(5.0, m.getRating());
        }
        db2.addSeries(series);
        for (Series m : db2.getSeries()) {
            assertEquals("Test", m.getName());
            assertEquals(1990, m.getReleaseYear());
            assertEquals(Arrays.asList("Genre"), m.getGenres());
            assertEquals(10, m.getSeasons());
            assertEquals(5.0, m.getRating());
        }

        // Resets savefile before ending:
        db2.removeSeries(series);
        finish();
    }

    private void finish() {
        // Writes original files back to savefile:
        for (Series m : series) {
            try {
                db.addSeries(m);
            } catch (Exception e) {
            }
        }
    }
}
