module moviedb.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires moviedb.core;
    requires java.net.http;

    opens moviedb.ui to javafx.graphics, javafx.fxml;
}