package moviedb.ui;

import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import moviedb.core.*;
import moviedb.json.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuControllerTest extends ApplicationTest {

    private MovieDatabase movieDatabase;
    private SeriesDatabase seriesDatabase;
    private List<AbstractMedia> mediaList = new ArrayList<>();
    private MenuController menuController;

    // Gets all movies and series from files, clears savefiles in case items were
    // added before tests were run to prevent conflicts
    @BeforeEach
    public void clearAndSaveOldMovies() {
        JsonFileHandler moviesJson = new JsonFileHandler(LocalDatabaseSaver.findPath("movies.json"));
        JsonFileHandler seriesJson = new JsonFileHandler(LocalDatabaseSaver.findPath("series.json"));
        try {
            movieDatabase = moviesJson.readMovieDatabase();
            movieDatabase.addObserver(new LocalDatabaseSaver());
            seriesDatabase = seriesJson.readSeriesDatabase();
            seriesDatabase.addObserver(new LocalDatabaseSaver());
            for (Movie movie : movieDatabase) {
                mediaList.add(movie);
            }
            for (Series series : seriesDatabase) {
                mediaList.add(series);
            }
            for (AbstractMedia media : mediaList) {
                if (media instanceof Series) {
                    seriesDatabase.removeSeries((Series) media);
                } else {
                    movieDatabase.removeMovie((Movie) media);
                }
            }
        } catch (IOException e) {
            fail();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        menuController = new MenuController();
        menuController.showStage();
    }

    // NB! clickOn-method does not support fx-ids with spaces in them, thus testing
    // is somewhat limited
    @Test
    public void testAddAndRemove() {
        addMedia("Test", "1999", "9.0", "90", false);
        clickOn("#movieTab");

        // Check if media has been added to GridPane and can be clicked
        Button button = (Button) this.menuController.getGridPane(false).getChildren().get(0);
        assertEquals("Test_1999_90_movie", button.getId().toString());
        assertDoesNotThrow(() -> {
            clickOn("#Test_1999_90_movie");
            clickOn("#deleteButton");
            assertDoesNotThrow(() -> {
                Node alert = lookup(".dialog-pane").query();
                from(alert.lookup("Something went wrong"));
                Node cancelButton = lookup("Cancel").query();
                clickOn(cancelButton);
            });
            clickOn("#closeButton");
        });

        // Add the same movie again, make sure error pop-up is shown:
        addMedia("Test", "1999", "9.0", "90", false);
        errorPopup();

        // Assert new movie not added to GridPane:

        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.menuController.getGridPane(false).getChildren().get(1);
        });

        // Add illegal movie:
        addMedia("Test", "2300", "15.0", "90", false);
        errorPopup();

        // Add series:
        addMedia("Test", "2000", "8.0", "8", true);

        // Check if movie has been added to GridPane and can be clicked
        Button button2 = (Button) this.menuController.getGridPane(true).getChildren().get(0);
        assertEquals("Test_2000_8_series", button2.getId().toString());

        // Add duplicate series:
        addMedia("Test", "2000", "8.0", "8", true);
        errorPopup();

        // Add illegal series:
        addMedia("Test", "200", "15.0", "100", true);
        errorPopup();

        // Delete added series
        assertDoesNotThrow(() -> {
            clickOn("#seriesTab");
            clickOn("#Test_2000_8_series");
            clickOn("#deleteButton");
            errorPopup();
        });

        // Delete added movies
        assertDoesNotThrow(() -> {
            clickOn("#movieTab");
            clickOn("#Test_1999_90_movie");
            clickOn("#deleteButton");
            errorPopup();
        });
    }

    private void addMedia(String name, String year, String rating, String runtimeSeasons, boolean isSeries) {
        clickOn("#addTab");
        if (isSeries) {
            clickOn("#radioSeries");
        } else {
            clickOn("#radioMovie");
        }
        clickOn("#nameField").write(name);
        clickOn("#releaseYearField").write(year);
        clickOn("#menuButton");
        clickOn("#ActionBox");
        clickOn("#menuButton");
        clickOn("#runtimeSeasonsField").write(runtimeSeasons);
        clickOn("#ratingField").write(rating);
        clickOn("#confirmButton");
    }

    @Test
    private void errorPopup() {
        assertDoesNotThrow(() -> {
            Node alert = lookup(".dialog-pane").query();
            from(alert.lookup("Something went wrong"));
            Node okButton = lookup("OK").query();
            clickOn(okButton);
        });
    }

    @Test
    public void testSort() {
        addMedia("A", "2000", "9.0", "90", false);
        addMedia("B", "2001", "10.0", "100", false);
        addMedia("A", "2000", "9.0", "9", true);
        addMedia("B", "2001", "10.0", "10", true);

        // Check if media has been added to GridPane and can be clicked
        Button button1 = (Button) this.menuController.getGridPane(false).getChildren().get(0);
        Button button2 = (Button) this.menuController.getGridPane(false).getChildren().get(1);
        Button button3 = (Button) this.menuController.getGridPane(true).getChildren().get(0);
        Button button4 = (Button) this.menuController.getGridPane(true).getChildren().get(1);

        clickOn("#movieTab");
        for (Object sortOption : this.menuController.getMovieSortOptions()) {
            testForSpecificSort(sortOption.toString(), button1, button2, false);
        }
        clickOn("#seriesTab");
        for (Object sortOption : this.menuController.getSeriesSortOptions()) {
            testForSpecificSort(sortOption.toString(), button3, button4, true);
        }

        // Delete added series
        assertDoesNotThrow(() -> {
            clickOn("#seriesTab");
            clickOn("#A_2000_9_series");
            clickOn("#deleteButton");
            errorPopup();
            clickOn("#B_2001_10_series");
            clickOn("#deleteButton");
            errorPopup();
        });

        // Delete added movies
        assertDoesNotThrow(() -> {
            clickOn("#movieTab");
            clickOn("#A_2000_90_movie");
            clickOn("#deleteButton");
            errorPopup();
            clickOn("#B_2001_100_movie");
            clickOn("#deleteButton");
            errorPopup();
        });
    }

    @Test
    private void testForSpecificSort(String prompt, Button button1, Button button2, boolean isSeries) {
        assertDoesNotThrow(() -> {
            if (isSeries) {
                clickOn("#seriesSortChoiceBox");
            } else {
                clickOn("#movieSortChoiceBox");
            }
            Node sortOption = lookup(prompt).query();
            clickOn(sortOption);
            if (isSeries) {
                clickOn("#seriesSortButton");
            } else {
                clickOn("#movieSortButton");
            }
            if (prompt.contains("(ascending)") || prompt.contains("(a-z)")) {
                assertEquals((Button) this.menuController.getGridPane(isSeries).getChildren().get(1), button2);
            } else {
                assertEquals((Button) this.menuController.getGridPane(isSeries).getChildren().get(1), button1);
            }
        });
    }

    @Test
    public void testInputChanged() {
        clickOn("#addTab");
        Button b = lookup("#confirmButton").query();
        assertTrue(b.isDisable());
        clickOn("#ratingField").write("5.0");
        assertTrue(b.isDisable());
        clickOn("#releaseYearField").write("2010");
        assertTrue(b.isDisable());
        clickOn("#runtimeSeasonsField").write("180");
        assertTrue(b.isDisable());
        clickOn("#nameField").write("Test");
        assertTrue(b.isDisable());
        clickOn("#menuButton");
        clickOn("#ActionBox");
        clickOn("#menuButton");
        assertFalse(b.isDisable());
        clickOn("#runtimeSeasonsField").eraseText(3);
        assertTrue(b.isDisable());
        clickOn("#runtimeSeasonsField").write("180");
        assertFalse(b.isDisable());
        clickOn("#releaseYearField").eraseText(4);
        assertTrue(b.isDisable());
    }

    @Test
    public void testRadioChanged() {
        clickOn("#addTab");
        clickOn("#radioMovie");
        Label runtime = lookup("#runtimeSeasonsLabel").query();
        TextField runtimeField = lookup("#runtimeSeasonsField").query();
        assertEquals("Runtime (min):", runtime.getText());
        assertEquals("Runtime", runtimeField.getPromptText());
        clickOn("#radioSeries");
        runtime = lookup("#runtimeSeasonsLabel").query();
        runtimeField = lookup("#runtimeSeasonsField").query();
        assertEquals("Seasons:", runtime.getText());
        assertEquals("Seasons", runtimeField.getPromptText());
    }
}
