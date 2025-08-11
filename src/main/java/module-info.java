module io.github.rajveer.dragonfly {
    requires javafx.controls;
    requires javafx.fxml;

    exports io.github.rajveer.dragonfly;
    opens io.github.rajveer.dragonfly to javafx.fxml;

    exports io.github.rajveer.dragonfly.gui to javafx.fxml;
    opens io.github.rajveer.dragonfly.gui to javafx.fxml;

    exports io.github.rajveer.dragonfly.utils;
    exports io.github.rajveer.dragonfly.ode;
    exports io.github.rajveer.dragonfly.systems;
}

/*
exports → makes the package available at compile time to other modules.

opens → allows reflection (used by FXML loader to call private/protected members, create instances, etc.).

In JavaFX, controllers must be opened to javafx.fxml so FXMLLoader can construct them.
 */