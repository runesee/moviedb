package moviedb.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import moviedb.core.AbstractMedia;
import moviedb.core.LocalDatabaseSaver;
import moviedb.core.Movie;
import moviedb.core.MovieDatabase;
import moviedb.core.MoviedbWebClient;
import moviedb.core.RemoteDatabaseSaver;
import moviedb.core.Series;
import moviedb.core.SeriesDatabase;
import moviedb.json.JsonFileHandler;

public class MenuController {

    // Defining FXML ID variables:
    @FXML
    private GridPane movieGridPane;
    @FXML
    private GridPane seriesGridPane;
    @FXML
    private Tab movieTab;
    @FXML
    private Tab seriesTab;
    @FXML
    private Tab addTab;
    @FXML
    private ScrollPane seriesScrollPane;
    @FXML
    private ScrollPane movieScrollPane;
    @FXML
    private ScrollPane addScrollPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private RadioButton radioMovie;
    @FXML
    private RadioButton radioSeries;
    @FXML
    private TextField nameField;
    @FXML
    private TextField releaseYearField;
    @FXML
    private TextField runtimeSeasonsField;
    @FXML
    private TextField ratingField;
    @FXML
    private Label runtimeSeasonsLabel;
    @FXML
    private ChoiceBox<String> movieSortChoiceBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> seriesSortChoiceBox = new ChoiceBox<>();
    @FXML
    private Button confirmButton;
    @FXML
    private MenuButton menuButton;
    @FXML
    private Button imageButton;

    private final String[] movieSortOptions = { "Sort by name (a-z)",
            "Sort by name (z-a)",
            "Sort by release year (ascending)",
            "Sort by release year (descending)",
            "Sort by runtime (ascending)",
            "Sort by runtime (descending)",
            "Sort by rating (ascending)",
            "Sort by rating (descending)"
    };
    private final String[] seriesSortOptions = { "Sort by name (a-z)",
            "Sort by name (z-a)",
            "Sort by release year (ascending)",
            "Sort by release year (descending)",
            "Sort by seasons (ascending)",
            "Sort by seasons (descending)",
            "Sort by rating (ascending)",
            "Sort by rating (descending)"
    };
    // Set of allowed genres, can be modified:
    private final String[] allowedGenres = {
            "Action",
            "Adventure",
            "Comedy",
            "Crime",
            "Drama",
            "Documentary",
            "Fantasy",
            "Horror",
            "Musical",
            "Romance",
            "Sci-Fi",
            "War",
            "Western"
    };

    private MovieDatabase movieDatabase;
    private SeriesDatabase seriesDatabase;
    private FXMLLoader fxmlLoader;
    private Stage currentStage;

    // Opting to use separate col and row values for movies and series.
    // Could alternatively have used only one pair of variables, as long
    // as they were reset after each for loop.
    private int colMovie = 0;
    private int colSeries = 0;
    private int rowSeries = 0;
    private int rowMovie = 0;

    private Map<Movie, Button> buttonMovieMap = new HashMap<>();
    private Map<Series, Button> buttonSeriesMap = new HashMap<>();
    private Map<AbstractMedia, Image> mediaImageMap = new HashMap<>();
    private ArrayList<Movie> movies = new ArrayList<>();
    private ArrayList<Series> series = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private Tab currentTab;
    private Image prevImage = null;

    /**
     * Method for initializing the movieDatabase and seriesDatabase objects based on
     * the savefiles.
     * Also adds the sort options to the choice boxes.
     */
    public void initialize() {
        // Add options to choice boxes:
        movieSortChoiceBox.getItems().addAll(movieSortOptions);
        movieSortChoiceBox.getSelectionModel().selectFirst();
        seriesSortChoiceBox.getItems().addAll(seriesSortOptions);
        seriesSortChoiceBox.getSelectionModel().selectFirst();

        boolean isRemote = false;
        MovieDatabase localMovieDatabase;
        SeriesDatabase localSeriesDatabase;
        MovieDatabase remoteMovieDatabase = null;
        SeriesDatabase remoteSeriesDatabase = null;

        // Read database from server:
        String baseUri = "http://localhost:8080/moviedb";
        MoviedbWebClient moviedbWebClient = new MoviedbWebClient(baseUri);
        if (moviedbWebClient.isRunning()) {
            isRemote = true;
            try {
                remoteMovieDatabase = moviedbWebClient.getMovieDatabase();
            } catch (IOException | InterruptedException e) {
                remoteMovieDatabase = new MovieDatabase();
                showErrorMessage("Could not retrieve movies from server!");
            }
            try {
                remoteSeriesDatabase = moviedbWebClient.getSeriesDatabase();
            } catch (IOException | InterruptedException e) {
                remoteSeriesDatabase = new SeriesDatabase();
                showErrorMessage("Could not retrieve series from server!");
            }
        }
        // Read database from file:
        JsonFileHandler movieFileHandler = new JsonFileHandler(LocalDatabaseSaver.findPath("movies.json"));
        JsonFileHandler seriesFileHandler = new JsonFileHandler(LocalDatabaseSaver.findPath("series.json"));
        try {
            localMovieDatabase = movieFileHandler.readMovieDatabase();
        } catch (IOException e) {
            localMovieDatabase = new MovieDatabase();
            showErrorMessage("Previously saved movies were not successfully imported!");
        }
        try {
            localSeriesDatabase = seriesFileHandler.readSeriesDatabase();
        } catch (IOException e) {
            localSeriesDatabase = new SeriesDatabase();
            showErrorMessage("Previously saved series were not successfully imported!");
        }

        if (isRemote) {
            movieDatabase = remoteMovieDatabase;
            movieDatabase.addObserver(new RemoteDatabaseSaver(baseUri));
            seriesDatabase = remoteSeriesDatabase;
            seriesDatabase.addObserver(new RemoteDatabaseSaver(baseUri));
        } else {
            movieDatabase = localMovieDatabase;
            seriesDatabase = localSeriesDatabase;
        }
        movieDatabase.addObserver(new LocalDatabaseSaver());
        seriesDatabase.addObserver(new LocalDatabaseSaver());

        if (remoteMovieDatabase != null && localMovieDatabase != null) {
            updateLocalMovies(localMovieDatabase, remoteMovieDatabase);
        }
        if (remoteSeriesDatabase != null && localSeriesDatabase != null) {
            updateLocalSeries(localSeriesDatabase, remoteSeriesDatabase);
        }
    }

    private void updateLocalMovies(MovieDatabase localMovieDatabase, MovieDatabase remoteMovieDatabase) {
        List<Movie> localMovies = localMovieDatabase.getMovies();
        List<Movie> remoteMovies = remoteMovieDatabase.getMovies();
        if (!localMovies.equals(remoteMovies)) {
            movieDatabase.setMovies(remoteMovies);
        }
    }

    private void updateLocalSeries(SeriesDatabase localSeriesDatabase, SeriesDatabase remoteSeriesDatabase) {
        List<Series> localSeries = localSeriesDatabase.getSeries();
        List<Series> remoteSeries = remoteSeriesDatabase.getSeries();
        if (!localSeries.equals(remoteSeries)) {
            seriesDatabase.setSeries(remoteSeries);
        }
    }

    /**
     * Creates an instance of a MenuController object.
     * Runs the initialize method, and sets the stage.
     * Adjusts scrollpane scrolling speed, and adds items to check boxes.
     */
    public MenuController() {
        initialize();
        currentStage = new Stage();
        fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource("menu.fxml"));
            fxmlLoader.setController(this);
            currentStage.setScene(new Scene(fxmlLoader.load()));
            currentStage.setTitle("MovieDB");
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentStage.sizeToScene();
        this.currentTab = this.tabPane.getSelectionModel().getSelectedItem();

        // Speeding up scrollpane scrolling speed as it is otherwise very slow:
        final double speed = 0.005;
        movieScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * speed;
            movieScrollPane.setVvalue(movieScrollPane.getVvalue() - deltaY);
        });
        seriesScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * speed;
            seriesScrollPane.setVvalue(seriesScrollPane.getVvalue() - deltaY);
        });
        addScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * speed;
            addScrollPane.setVvalue(addScrollPane.getVvalue() - deltaY);
        });

        // Add genre options to dropdown menu:
        Collection<CustomMenuItem> menuItems = new ArrayList<>();
        for (String string : this.allowedGenres) {
            CheckBox checkBox = new CheckBox(string);
            this.checkBoxes.add(checkBox);
            checkBox.setOnAction((event) -> {
                handleInputChanged();
            });
            checkBox.setId(string + "Box");
            CustomMenuItem customMenuItem = new CustomMenuItem(checkBox);
            customMenuItem.setHideOnClick(false);
            menuItems.add(customMenuItem);
        }
        menuButton.getItems().addAll(menuItems);
    }

    /**
     * Method for adding buttons to UI upon initialization.
     * Buttons are added based on media in the databases.
     */
    public void initializeUI() {
        // Find width of current stage, reset variables:
        int width = (int) Math.floor(currentStage.getWidth() / 250);
        this.colMovie = 0;
        this.rowMovie = 0;
        this.rowSeries = 0;
        this.colSeries = 0;

        for (Movie movie : this.movieDatabase.getMovies()) {
            if (colMovie != 0 && width != 0) {
                if (colMovie % width == 0) {
                    colMovie = 0;
                    rowMovie++;
                }
            }
            addButtonToUI(movie, false, null);
            colMovie++;
        }
        for (Series series : this.seriesDatabase.getSeries()) {
            if (colSeries != 0 && width != 0) {
                if (colSeries % width == 0) {
                    colSeries = 0;
                    rowSeries++;
                }
            }
            addButtonToUI(series, true, null);
            colSeries++;
        }
    }

    /**
     * Method for adding a button to the corresponding gridPane in the UI.
     * Gives the button an image as graphic if image is not null.
     * 
     * @param media    the abstract media to be added
     * @param isSeries whether or not the media is instance of series
     * @param image    image to give the button as graphic, can be null
     */
    private void addButtonToUI(AbstractMedia media, boolean isSeries, Image image) {
        Button button = new Button();
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setPrefHeight(300);
        button.setPrefWidth(200);
        button.setMinHeight(300);
        button.setMinWidth(200);

        if (isSeries) {
            Series castedSeries = (Series) media;
            this.buttonSeriesMap.put(castedSeries, button);
            this.series.add(castedSeries);
            button.setId(castedSeries.getName() + "_" + castedSeries.getReleaseYear() + "_" + castedSeries.getSeasons()
                    + "_" + "series");
        } else {
            Movie castedMovie = (Movie) media;
            this.buttonMovieMap.put(castedMovie, button);
            this.movies.add(castedMovie);
            button.setId(castedMovie.getName() + "_" + castedMovie.getReleaseYear() + "_" + castedMovie.getRuntime()
                    + "_" + "movie");
        }
        button.setOnAction(event -> handleButtonClicked(event));
        String path;
        try {
            if (image == null) {
                path = LocalDatabaseSaver.imageDirWithSeparator + button.getId() + ".jpg";
                image = new Image(path);
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(300);
            button.setGraphic(imageView);
        } catch (Exception e) {
            // If no image is found, sets the name of the media on the button instead
            button.setText(media.getName());
        }
        button.setCursor(Cursor.HAND);
        if (isSeries) {
            seriesGridPane.add(button, this.colSeries, this.rowSeries);
        } else {
            movieGridPane.add(button, this.colMovie, this.rowMovie);
        }
    }

    /**
     * Method for saving an image to the user's home repo.
     * 
     * @param media the media whose image should be saved
     * @return the image that was saved
     */
    private Image saveMedia(AbstractMedia media) {
        Path source = Paths.get(this.mediaImageMap.get(media).getUrl());
        Path targetDir = Paths.get(LocalDatabaseSaver.imageDir);

        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path target = null;
        if (this.radioMovie.isSelected()) {
            target = targetDir.resolve(media.getName() + "_" + media.getReleaseYear() + "_"
                    + ((Movie) media).getRuntime() + "_" + "movie" + ".jpg");
        } else {
            target = targetDir.resolve(media.getName() + "_" + media.getReleaseYear() + "_"
                    + ((Series) media).getSeasons() + "_" + "series" + ".jpg");
        }
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = new Image(this.mediaImageMap.get(media).getUrl(), 200, 300, false, true, true);
        return image;
    }

    private void deleteMedia(AbstractMedia media) {
        try {
            if (media instanceof Series) {
                Files.deleteIfExists(Paths.get(LocalDatabaseSaver.imageDirWithSeparator + media.getName() + "_"
                        + media.getReleaseYear() + "_" + ((Series) media).getSeasons() + "_" + "series" + ".jpg"));
            } else {
                Files.deleteIfExists(Paths.get(LocalDatabaseSaver.imageDirWithSeparator + media.getName() + "_"
                        + media.getReleaseYear() + "_" + ((Movie) media).getRuntime() + "_" + "movie" + ".jpg"));
            }
        } catch (NoSuchFileException e) {
            showErrorMessage("No such file/directory exists");
        } catch (DirectoryNotEmptyException e) {
            showErrorMessage("Directory is not empty.");
        } catch (IOException e) {
            showErrorMessage("Invalid permissions.");
        }
    }

    @FXML
    // Method that resizes and updates UI
    private void resizeUI() {
        // Find width of current stage, reset variables:
        int width = (int) Math.floor(currentStage.getWidth() / 250);
        this.colMovie = 0;
        this.rowMovie = 0;
        this.colSeries = 0;
        this.rowSeries = 0;

        try {
            this.movieGridPane.getChildren().clear();
            this.seriesGridPane.getChildren().clear();
        } catch (NullPointerException e) {
            // Temporary try-catch, not sure if neccesary later on
        }

        for (Movie movie : this.movies) {
            if (colMovie != 0 && width != 0) {
                if (colMovie % width == 0) {
                    colMovie = 0;
                    rowMovie++;
                }
            }
            Button button = this.buttonMovieMap.get(movie);
            movieGridPane.add(button, colMovie, rowMovie);
            colMovie++;
        }

        for (Series series : this.series) {
            if (colSeries != 0 && width != 0) {
                if (colSeries % width == 0) {
                    colSeries = 0;
                    rowSeries++;
                }
            }
            Button button = this.buttonSeriesMap.get(series);
            seriesGridPane.add(button, colSeries, rowSeries);
            colSeries++;
        }
    }

    @FXML
    void handleButtonClicked(ActionEvent event) {
        Button button = (Button) event.getSource();
        this.currentTab = this.tabPane.getSelectionModel().getSelectedItem();

        String path = LocalDatabaseSaver.imageDirWithSeparator + button.getId() + ".jpg";
        if (this.currentTab.getText().equals("Movies")) {
            for (Movie movie : this.buttonMovieMap.keySet()) {
                if (this.buttonMovieMap.get(movie) == button) {
                    MediaController mediaController = new MediaController(movie, this.currentTab, path, this);
                    mediaController.showStage();
                }
            }
        } else {
            for (Series series : this.buttonSeriesMap.keySet()) {
                if (this.buttonSeriesMap.get(series) == button) {
                    MediaController mediaController = new MediaController(series, this.currentTab, path, this);
                    mediaController.showStage();
                }
            }
        }
    }

    /**
     * Method that removes the selected media (Movie or Series).
     * Clears the related variables and deletes related image from user's home repo.
     * Resizes the UI after removing button.
     * 
     * @param media the media to be removed from the database
     */
    public void handleRemove(AbstractMedia media) {
        if (media instanceof Movie) {
            this.buttonMovieMap.get((Movie) media).setOnAction(null);
            ;
            Movie movie = (Movie) media;
            this.movieDatabase.removeMovie(movie);
            this.movieGridPane.getChildren().remove(this.buttonMovieMap.get(movie));

            // Removing media from buttonMap causes ConcurrentModificationException.
            // Exception is fired even when method surrounded with try-catch, therefore
            // choosing to leave map as it is.
            // Not removing objects should have no effect, as newly added objects are always
            // unique, however this does leave
            // unused objects in the map:
            // this.buttonMovieMap.remove(movie);

            this.mediaImageMap.remove(movie);
            this.movies.remove(movie);
        } else {
            Series series = (Series) media;
            this.seriesDatabase.removeSeries(series);
            this.seriesGridPane.getChildren().remove(this.buttonSeriesMap.get(series));
            // this.buttonSeriesMap.remove(series); // See comment paragraph above
            this.mediaImageMap.remove(series);
            this.series.remove(series);
        }
        deleteMedia(media);
        resizeUI();
    }

    @FXML
    // Method that handles the sort buttom and all of its possibilities
    void handleSort(ActionEvent event) {
        String sortOption;
        this.currentTab = this.tabPane.getSelectionModel().getSelectedItem();
        if (this.currentTab.getText().equals("Movies")) {
            sortOption = movieSortChoiceBox.getValue();
        } else {
            sortOption = seriesSortChoiceBox.getValue();
        }
        Comparator<AbstractMedia> comp = null;
        switch (sortOption) {
            case "Sort by name (a-z)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return m0.getName().compareTo(m1.getName());
                    }
                };
                break;
            case "Sort by name (z-a)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return m1.getName().compareTo(m0.getName());
                    }
                };
                break;
            case "Sort by release year (ascending)":
                comp = (new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return m0.getReleaseYear() - m1.getReleaseYear();
                    }
                });
                break;
            case "Sort by release year (descending)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return m1.getReleaseYear() - m0.getReleaseYear();
                    }
                };
                break;
            case "Sort by runtime (ascending)":
                if (this.currentTab.getText().equals("Movies")) {
                    comp = new Comparator<AbstractMedia>() {
                        @Override
                        public int compare(AbstractMedia m0, AbstractMedia m1) {
                            return ((Movie) m0).getRuntime() - ((Movie) m1).getRuntime();
                        }
                    };
                }
                break;
            case "Sort by runtime (descending)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return ((Movie) m1).getRuntime() - ((Movie) m0).getRuntime();
                    }
                };
                break;
            case "Sort by seasons (ascending)":
                if (this.currentTab.getText().equals("Series")) {
                    comp = new Comparator<AbstractMedia>() {
                        @Override
                        public int compare(AbstractMedia m0, AbstractMedia m1) {
                            return ((Series) m0).getSeasons() - ((Series) m1).getSeasons();
                        }
                    };
                }
                break;
            case "Sort by seasons (descending)":
                if (this.currentTab.getText().equals("Series")) {
                    comp = new Comparator<AbstractMedia>() {
                        @Override
                        public int compare(AbstractMedia m0, AbstractMedia m1) {
                            return ((Series) m1).getSeasons() - ((Series) m0).getSeasons();
                        }
                    };
                }
                break;
            case "Sort by rating (ascending)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return (int) Math.floor(m0.getRating() - m1.getRating());
                    }
                };
                break;
            case "Sort by rating (descending)":
                comp = new Comparator<AbstractMedia>() {
                    @Override
                    public int compare(AbstractMedia m0, AbstractMedia m1) {
                        return (int) Math.floor(m1.getRating() - m0.getRating());
                    }
                };
                break;
            default:
                break;
        }
        if (comp != null) {
            if (this.currentTab.getText().equals("Series")) {
                this.series.sort(comp);
            } else {
                this.movies.sort(comp);
            }
            resizeUI();
        }
    }

    /**
     * Method for showing the stage, called by App.java.
     * Shows the stage and initialized the UI.
     * Adds a change listener to the stage size being changed, which resized the UI.
     */
    public void showStage() {
        currentStage.show();
        currentStage.setMaximized(true);
        currentStage.setMinWidth(300);
        currentStage.setMinHeight(300);
        this.initializeUI();
        currentStage.widthProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
                    Number newSceneWidth) {
                resizeUI();
            }
        });
    }

    /**
     * Method for changing the name of labels when selected radio button is changed.
     */
    @FXML
    public void handleRadio() {
        if (radioMovie.isSelected()) {
            runtimeSeasonsLabel.setText("Runtime (min):");
            runtimeSeasonsField.setPromptText("Runtime");
        } else {
            runtimeSeasonsLabel.setText("Seasons:");
            runtimeSeasonsField.setPromptText("Seasons");
        }
    }

    /**
     * Method for checking whether all the input fields are empty.
     * If fields are not empty, enable the confirm button.
     * Otherwise, disable the confirm button.
     */
    @FXML
    public void handleInputChanged() {
        boolean selected = false;
        for (CheckBox checkBox : this.checkBoxes) {
            if (checkBox.isSelected()) {
                selected = true;
            }
        }
        if (!(this.nameField.getText().equals(""))
                && !(this.ratingField.getText().equals(""))
                && selected
                && !(this.runtimeSeasonsField.getText().equals(""))
                && !(this.releaseYearField.getText().equals(""))) {
            this.confirmButton.setDisable(false);
            this.imageButton.setDisable(false);
        }
        if (!(this.nameField.getText().equals("")) && !(this.ratingField.getText().equals(""))
                && selected
                && !(this.runtimeSeasonsField.getText().equals(""))
                && !(this.releaseYearField.getText().equals(""))) {
            this.confirmButton.setDisable(false);
        } else {
            this.confirmButton.setDisable(true);
            this.imageButton.setDisable(true);
        }
    }

    @FXML
    // Method for adding new movie
    void handleAdd(ActionEvent event) {
        boolean added;
        List<String> genres = new ArrayList<>();
        for (CheckBox checkBox : this.checkBoxes) {
            if (checkBox.isSelected()) {
                genres.add(checkBox.getText());
            }
        }
        if (this.radioMovie.isSelected()) {
            Movie movie;
            try {
                movie = new Movie(nameField.getText(),
                        Integer.parseInt(releaseYearField.getText()),
                        genres,
                        Integer.parseInt(runtimeSeasonsField.getText()),
                        Double.parseDouble(ratingField.getText()));
            } catch (IllegalArgumentException e) {
                showErrorMessage(e.getLocalizedMessage());
                clearFields();
                return;
            }
            added = movieDatabase.addMovie(movie);
            if (!added) {
                showErrorMessage("Movie " + movie.getName() + " is already registered!");
            } else {
                if (this.prevImage != null) {
                    this.mediaImageMap.put(movie, prevImage);
                    saveMedia(movie);
                    addButtonToUI(movie, false, prevImage);
                } else {
                    addButtonToUI(movie, false, null);
                }
            }
        } else {
            Series series;
            try {
                series = new Series(nameField.getText(),
                        Integer.parseInt(releaseYearField.getText()),
                        genres,
                        Integer.parseInt(runtimeSeasonsField.getText()),
                        Double.parseDouble(ratingField.getText()));
            } catch (IllegalArgumentException e) {
                showErrorMessage(e.getLocalizedMessage());
                clearFields();
                return;
            }
            added = seriesDatabase.addSeries(series);
            if (!added) {
                showErrorMessage("Series " + series.getName() + " is already registered!");
            } else {
                if (this.prevImage != null) {
                    this.mediaImageMap.put(series, prevImage);
                    saveMedia(series);
                    addButtonToUI(series, true, prevImage);
                } else {
                    addButtonToUI(series, true, null);
                }
            }
        }
        // Clear fields and resize after add:
        clearFields();
        resizeUI();
    }

    private void clearFields() {
        nameField.setText("");
        releaseYearField.setText("");
        ratingField.setText("");
        runtimeSeasonsField.setText("");
        for (CheckBox checkBox : this.checkBoxes) {
            checkBox.setSelected(false);
        }
        this.prevImage = null;
    }

    /**
     * Method for letting user select an image to add to a media.
     * Image is selected through a FileChooser.
     * Sets the prevImage variable to the chosen image.
     * If no image is chosen, prevImage is set to null.
     */
    @FXML
    public void handleImage() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        try {
            Image img = new Image(file.getCanonicalPath());
            this.prevImage = img;
        } catch (Exception e) {
            showErrorMessage("Could not find image");
        }
    }

    @FXML
    // Method that generates an error-popup given illegal inputs
    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Method that returns the seriesGridPane or movieGridPane.
     * 
     * @param isSeries boolean based on whether or not media is a series
     * @return seriesGridPane if isSeries, movieGridPane otherwise
     */
    public GridPane getGridPane(boolean isSeries) {
        if (isSeries) {
            return this.seriesGridPane;
        }
        return this.movieGridPane;
    }

    public String[] getMovieSortOptions() {
        return movieSortOptions;
    }

    public String[] getSeriesSortOptions() {
        return seriesSortOptions;
    }

}
