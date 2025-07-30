module io.github.rajveer.dragonfly {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.rajveer.dragonfly to javafx.fxml;
    exports io.github.rajveer.dragonfly;
}