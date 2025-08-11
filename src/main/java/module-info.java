module io.github.rajveer.dragonfly {
    requires javafx.controls;
    requires javafx.fxml;

    exports io.github.rajveer.dragonfly;
    opens io.github.rajveer.dragonfly to javafx.fxml;

    exports io.github.rajveer.dragonfly.gui to javafx.fxml; // allow FXML loader to access controllers
    opens io.github.rajveer.dragonfly.gui to javafx.fxml; // also open for reflection

    exports io.github.rajveer.dragonfly.utils;
    exports io.github.rajveer.dragonfly.ode;
    exports io.github.rajveer.dragonfly.systems;
}