package moviedb.ui;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        MenuController menuController = new MenuController();
        menuController.showStage();
    }

    public static void main(String[] args) {
        launch();
    }

}