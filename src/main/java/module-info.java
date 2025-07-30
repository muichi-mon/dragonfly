module io.github.rajveer.dragonfly {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.rajveer.dragonfly to javafx.fxml;
    exports io.github.rajveer.dragonfly;
    exports io.github.rajveer.dragonfly.utils;
    exports io.github.rajveer.dragonfly.ode;
    exports io.github.rajveer.dragonfly.systems;
}